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
package edu.emory.mathcs.nlp.learning.util;

import org.apache.commons.math3.util.FastMath;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MLUtils
{
	/** Transform the scores into softmax regression. */
	static public void softmax(float[] scores)
	{
		float sum = 0;
		
		for (int i=0; i<scores.length; i++)
		{
			scores[i] = (float)FastMath.exp(scores[i]);
			sum += scores[i];
		}
		
		for (int i=0; i<scores.length; i++)
			scores[i] /= sum;
	}
	
	static public int argmax(float[] scores)
	{
		return argmax(scores, scores.length);
	}
	
	static public int argmax(float[] scores, int size)
	{
		int i, maxIndex = 0;
		double maxValue = scores[maxIndex];
		
		for (i=1; i<size; i++)
		{
			if (maxValue < scores[i])
			{
				maxIndex = i;
				maxValue = scores[maxIndex];
			}
		}
		
		return maxIndex;
	}
	
	static public int argmax(float[] scores, IntCollection labels)
	{
		if (labels == null || labels.isEmpty()) return argmax(scores);
		float maxValue = -Float.MAX_VALUE;
		int   maxIndex = -1;
		
		for (int i : labels)
		{
			if (maxValue < scores[i])
			{
				maxIndex = i;
				maxValue = scores[i];
			}
		}
		
		return maxIndex;
	}
	
	static public int[] argmax2(float[] scores)
	{
		return argmax2(scores, scores.length);
	}
	
	static public int[] argmax2(float[] scores, int size)
	{
		if (size < 2) return new int[]{0,-1};
		int[] max = {0,1};
		
		if (scores[0] < scores[1])
		{
			max[0] = 1;
			max[1] = 0;
		}
		
		for (int i=2; i<size; i++)
		{
			if (scores[max[0]] < scores[i])
			{
				max[1] = max[0];
				max[0] = i;
			}
			else if (scores[max[1]] < scores[i])
				max[1] = i;
		}
		
		return max;
	}
	
	static public int[] argmax2(float[] scores, IntCollection labels)
	{
		if (labels == null || labels.isEmpty()) return argmax2(scores);
		IntIterator it = labels.iterator();
		if (labels.size() < 2) return new int[]{it.nextInt(),-1};
		int[] max = {it.nextInt(), it.nextInt()};
		
		if (scores[max[0]] < scores[max[1]])
			DSUtils.swap(max, 0, 1);
		
		while (it.hasNext())
		{
			int i = it.nextInt();
			
			if (scores[max[0]] < scores[i])
			{
				max[1] = max[0];
				max[0] = i;
			}
			else if (scores[max[1]] < scores[i])
				max[1] = i;
		}
		
		return max;
	}
}
