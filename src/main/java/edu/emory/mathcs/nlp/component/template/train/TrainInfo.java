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
package edu.emory.mathcs.nlp.component.template.train;

import java.util.Random;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class TrainInfo
{
	private float  rollin_init, rollin_current;
	private int    batch_size;
	private Random random;
	
	public TrainInfo(int batchSize, float rollInProbability)
	{
		random = new XORShiftRandom(9);
		setBatchSize(batchSize);
		setRollInProbability(rollInProbability);
	}
	
	public int getBatchSize()
	{
		return batch_size;
	}

	public void setBatchSize(int size)
	{
		batch_size = size;
	}
	
	public float getRollInProbability()
	{
		return rollin_current;
	}
	
	public void setRollInProbability(float probability)
	{
		rollin_init = rollin_current = probability;
	}

	public void updateRollInProbability()
	{
		rollin_current *= rollin_init;
	}
	
	public boolean chooseGold()
	{
		return (rollin_current > 0) && (rollin_current >= 1 || rollin_current > random.nextDouble());
	}
}
