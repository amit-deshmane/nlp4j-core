/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.bin;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.OnlineComponent;
import edu.emory.mathcs.nlp.component.template.eval.Eval;
import edu.emory.mathcs.nlp.component.template.node.AbstractNLPNode;
import edu.emory.mathcs.nlp.component.template.reader.NLPReader;
import edu.emory.mathcs.nlp.component.template.reader.TSVReader;
import edu.emory.mathcs.nlp.component.template.state.NLPState;
import edu.emory.mathcs.nlp.component.template.util.GlobalLexica;
import edu.emory.mathcs.nlp.component.template.util.NLPFlag;
import edu.emory.mathcs.nlp.learning.optimization.OnlineOptimizer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class ModelReduce
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-m", usage="model file (required)", required=true, metaVar="<filename>")
	public String model_file;
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	@Option(name="-oe", usage="output file extension (default: sk)", required=false, metaVar="<string>")
	public String output_ext = "red";
	@Option(name="-start", usage="starting reduce rate (default: 0.05)", required=false, metaVar="<float>")
	public float start = 0.05f;
	@Option(name="-inc", usage="incremental reduce rate (default: 0.01)", required=false, metaVar="<float>")
	public float increment = 0.01f;
	@Option(name="-lower", usage="lower bound (required)", required=true, metaVar="<float>")
	public float lower_bound;
	@Option(name="-save", usage="save the model using the starting rate (default: false)", required=false, metaVar="<boolean>")
	public boolean save = false;
	
	public <N,S>ModelReduce() {}
	
	@SuppressWarnings("unchecked")
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>ModelReduce(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		GlobalLexica<N> lexica = new GlobalLexica<>(IOUtils.createFileInputStream(configuration_file));
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		OnlineComponent<N,S> component = (OnlineComponent<N,S>)in.readObject(); in.close();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
		List<String> inputFiles = FileUtils.getFileList(input_path, input_ext);
		OnlineOptimizer optimizer = component.getOptimizer();
		double currScore;
		
		if (save)
		{
			component.getFeatureTemplate().reduce(optimizer.getWeightVector(), start);
			evaluate(inputFiles, component, lexica, start);
			ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(model_file+"."+output_ext);
			fout.writeObject(component);
			fout.close();
		}
		else
		{
			evaluate(inputFiles, component, lexica, 0f);
			
			for (float f=start; ; f+=increment)
			{
				component.getFeatureTemplate().reduce(optimizer.getWeightVector(), f);
				currScore = evaluate(inputFiles, component, lexica, f);
				if (lower_bound >= currScore) break;
			}			
		}
	}
	
	public <N extends AbstractNLPNode<N>, S extends NLPState<N>>double evaluate(List<String> inputFiles, OnlineComponent<N,S> component, GlobalLexica<N> lexica, float rate) throws Exception
	{
		TSVReader<N> reader = createTSVReader(component.getConfiguration().getReaderFieldMap());
		long st, et, ttime = 0, tnode = 0;
		List<N[]> document;
		N[] nodes;
		
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			if (component.isDocumentBased())
			{
				document = reader.readDocument();
				lexica.process(document);
				st = System.currentTimeMillis();
				component.process(document);
				et = System.currentTimeMillis();
				ttime += et - st;
				tnode++;
			}

			else
			{
				while ((nodes = reader.next()) != null)
				{
					lexica.process(nodes);
					st = System.currentTimeMillis();
					component.process(nodes);
					et = System.currentTimeMillis();
					ttime += et - st;
					tnode += nodes.length - 1;
				}	
			}
			
			reader.close();
		}
		
		System.out.println(String.format("%5.4f: %s -> %7d, %3d, N/S = %d", rate, eval.toString(), component.getFeatureTemplate().getSparseFeatureSize(), component.getOptimizer().getLabelSize(), (int)Math.round(1000d * tnode / ttime)));
		return eval.score();
	}
	
	@SuppressWarnings("unchecked")
	public <N extends AbstractNLPNode<N>>TSVReader<N> createTSVReader(Object2IntMap<String> map)
	{
		return (TSVReader<N>)new NLPReader(map);
	}
	
	static public void main(String[] args)
	{
		try
		{
			new ModelReduce(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
