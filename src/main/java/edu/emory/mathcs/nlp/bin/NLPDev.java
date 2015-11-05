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
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.common.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.common.eval.Eval;
import edu.emory.mathcs.nlp.component.common.node.NLPNode;
import edu.emory.mathcs.nlp.component.common.reader.TSVReader;
import edu.emory.mathcs.nlp.component.common.state.NLPState;
import edu.emory.mathcs.nlp.component.common.util.NLPFlag;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPDev
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-m", usage="model file (required)", required=true, metaVar="<filename>")
	public String model_file;
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	public String input_path;
	@Option(name="-ie", usage="input file extension (default: *)", required=false, metaVar="<string>")
	public String input_ext = "*";
	@Option(name="-oe", usage="output file extension (default: cnlp)", required=false, metaVar="<string>")
	public String output_ext = "cnlp";
	
	public <N,S>NLPDev() {}
	
	@SuppressWarnings("unchecked")
	public <S extends NLPState>NLPDev(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		
		ObjectInputStream in = IOUtils.createObjectXZBufferedInputStream(model_file);
		NLPOnlineComponent<S> component = (NLPOnlineComponent<S>)in.readObject();
		component.setConfiguration(IOUtils.createFileInputStream(configuration_file));
//		List<String> inputFiles = FileUtils.getFileList(input_path, input_ext);
		
		
		ObjectOutputStream fout = IOUtils.createObjectXZBufferedOutputStream(model_file+"."+output_ext);
		fout.writeObject(component);
		fout.close();
	}
	
	public <S extends NLPState>double evaluate(List<String> inputFiles, NLPOnlineComponent<S> component, StringModel model, float rate) throws Exception
	{
		TSVReader reader = component.getConfiguration().getTSVReader();
		NLPNode[] nodes;
		
		component.setFlag(NLPFlag.EVALUATE);
		Eval eval = component.getEval();
		eval.clear();
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			while ((nodes = reader.next()) != null)
				component.process(nodes);
			
			reader.close();
		}
		
		System.out.println(String.format("%5.4f: %s -> %d", rate, eval.toString(), model.getFeatureSize()));
		return eval.score();
	}
	
	static public void main(String[] args)
	{
		try
		{
			new NLPDev(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
