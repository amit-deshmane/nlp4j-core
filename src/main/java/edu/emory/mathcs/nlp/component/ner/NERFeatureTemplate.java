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

import java.util.Collection;

import edu.emory.mathcs.nlp.component.zzz.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.zzz.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.zzz.node.NLPNode;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NERFeatureTemplate extends FeatureTemplate<NERState>
{
	private static final long serialVersionUID = 2750773840515707758L;

	public NERFeatureTemplate()	
	{
		super();
	}
	
//	========================= FEATURE EXTRACTORS =========================
	
	@Override
	protected String getFeature(FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		if (node == null) return null;
		
		switch (item.field)
		{
		case ambiguity_class: return null; // To be filled.
		default: return getFeature(item, node);
		}
	}
	
	@Override
	protected Collection<String> getFeatures(FeatureItem<?> item)
	{
		NLPNode node = state.getNode(item);
		return (node == null) ? null : getFeatures(item, node);
	}
}
