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
package edu.emory.mathcs.nlp.component.ner;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.component.util.NLPOnlineComponent;
import edu.emory.mathcs.nlp.component.util.config.NLPConfig;
import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.eval.F1Eval;
import edu.emory.mathcs.nlp.component.util.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTagger<N extends NLPNode> extends NLPOnlineComponent<N,NERState<N>>
{
	private static final long serialVersionUID = 87807440372806016L;

	public NERTagger() {}
	
	public NERTagger(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== LEXICONS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException
	{
		
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	@SuppressWarnings("unchecked")
	public void setConfiguration(InputStream in)
	{
		setConfiguration((NLPConfig<N>)new NERConfig(in));
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}
	
	@Override
	protected NERState<N> initState(N[] nodes)
	{
		return new NERState<>(nodes);
	}
}
