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
package edu.emory.mathcs.nlp.component.dep;

import java.io.Serializable;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learning.util.StringPrediction;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLabel implements Serializable, Comparable<DEPLabel>
{
	private static final long serialVersionUID = -7214636048814903365L;
	private static final String DELIM = StringConst.UNDERSCORE;
	
	private String s_arc;
	private String s_list;
	private String s_deprel;
	private float  d_score;
	
	public DEPLabel() {}
	
	public DEPLabel(String arc, String list, String deprel)
	{
		setArc(arc);
		setList(list);
		setDeprel(deprel);
	}
	
	public DEPLabel(String label)
	{
		set(label, 0);
	}
	
	public DEPLabel(StringPrediction p)
	{
		set(p.getLabel(), p.getScore());
	}
	
	public void set(String label, float score)
	{
		int idx1 = label.indexOf(DELIM);
		int idx2 = label.lastIndexOf(DELIM);
		
		setArc   (label.substring(0, idx1));
		setList  (label.substring(idx1+1, idx2));
		setDeprel(label.substring(idx2+1));
		setScore (score);
	}
	
	public String getArc()
	{
		return s_arc;
	}
	
	public String getList()
	{
		return s_list;
	}
	
	public String getDeprel()
	{
		return s_deprel;
	}
	
	public float getScore()
	{
		return d_score;
	}
	
	public void setArc(String arc)
	{
		s_arc = arc;
	}
	
	public void setList(String list)
	{
		s_list = list;
	}
	
	public void setDeprel(String deprel)
	{
		s_deprel = deprel;
	}
	
	public void setScore(float score)
	{
		d_score = score;
	}
	
	public boolean isArc(String label)
	{
		return s_arc.equals(label);
	}
	
	public boolean isArc(DEPLabel label)
	{
		return isArc(label.getArc());
	}
	
	public boolean isList(String label)
	{
		return s_list.equals(label);
	}
	
	public boolean isList(DEPLabel label)
	{
		return isList(label.getList());
	}
	
	public boolean isDeprel(String label)
	{
		return s_deprel.equals(label);
	}
	
	public boolean equalsAll(DEPLabel label)
	{
		return isArc(label.s_arc) && isList(label.s_list) && isDeprel(label.s_deprel);
	}
	
	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(DELIM);
		
		join.add(s_arc);
		join.add(s_list);
		join.add(s_deprel);
		
		return join.toString();
	}

	@Override
	public int compareTo(DEPLabel o)
	{
		return MathUtils.signum(d_score - o.d_score);
	}
}
