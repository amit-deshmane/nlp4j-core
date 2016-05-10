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
package edu.emory.mathcs.nlp.learning.neural;

import java.util.Arrays;

import edu.emory.mathcs.nlp.learning.activation.ActivationFunction;
import edu.emory.mathcs.nlp.learning.activation.SoftmaxFunction;
import edu.emory.mathcs.nlp.learning.initialization.WeightGenerator;
import edu.emory.mathcs.nlp.learning.util.FeatureVector;
import edu.emory.mathcs.nlp.learning.util.Instance;
import edu.emory.mathcs.nlp.learning.util.MajorVector;
import edu.emory.mathcs.nlp.learning.util.SparseItem;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 * @author amit-deshmane ({@code amitad87@gmail.com})
 */
public class FeedForwardNeuralNetworkSoftmax extends FeedForwardNeuralNetwork
{
	private static final long serialVersionUID = 7122005284712284931L;

	public FeedForwardNeuralNetworkSoftmax(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator initializer)
	{
		super(hiddenDimensions, functions, learningRate, bias, initializer);
	}
	
	public FeedForwardNeuralNetworkSoftmax(int[] hiddenDimensions, ActivationFunction[] functions, float learningRate, float bias, WeightGenerator initializer, float [] dropout_prob)
	{
		super(hiddenDimensions, functions, learningRate, bias, initializer, dropout_prob);
	}
	
//	============================== OVERRIDE ==============================
	
	@Override
	protected ActivationFunction createActivationFunctionH2O()
	{
		return new SoftmaxFunction();
	}
	
	@Override
	protected int getPredictedLabel(Instance instance)
	{
		return getPredictedLabelRegression(instance);
	}
	
	@Override
	protected float getLearningRate(int index, boolean sparse)
	{
		return learning_rate;
	}
	
	@Override
	public void updateMiniBatch() {}
	
//	============================== BACKWARD PROPAGATION ==============================
	/**
	 * Found a minor bug:<br>
	 * Need to check if the weights of the connections from bias units get updated in back propagation or not.<br>
	 */

	@Override
	protected float[] backwardPropagationO2H(Instance instance, float[] input)
	{
		float[] output = Arrays.copyOf(instance.getScores(), getLabelSize());
		float[] gradients = getGradientsRegression(instance);
		for(int index = 0; index < gradients.length; index++){
			gradients[index] = -1 * gradients[index];
		}
		float[] errors = new float[input.length];
		int index;
		
		MajorVector weights = w_h2o.getDenseWeightVector();
		
		for (int y=0; y<gradients.length; y++)
		{
			for (int xi=0; xi<input.length; xi++)
			{
				// notice the index is [1 + xi], 1 is for bias unit which is in sparce vector
				if(sampled_thinned_network[sampled_thinned_network.length -1][1 + xi]){
					index = weights.indexOf(y, xi);
					errors[xi] += gradients[y] * output[y] * weights.get(index);
					weights.add(index, -1 * getLearningRate(index, false) * gradients[y] * input[xi]);
				}
			}
		}

		return errors;
	}

	@Override
	protected float[] backwardPropagationH2H(MajorVector weights, float[] gradients, float[] input, float[] output, int layer)
	{
		float[] errors = new float[input.length];
		int index;
		
		for (int y=0; y<gradients.length; y++)
		{
			for (int xi=0; xi<input.length; xi++)
			{
				if(sampled_thinned_network[layer + 1][1 + xi] && sampled_thinned_network[layer + 2][1 + y]){
					index = weights.indexOf(y, xi);
					errors[xi] += gradients[y] * weights.get(index);
					weights.add(index, -1 * getLearningRate(index, false) * gradients[y] * input[xi]);
				}
			}
		}
		
		return errors;
	}
	
	@Override
	protected void backwardPropagationH2I(FeatureVector input, float[] gradients, float[] output)
	{
		MajorVector weights;
		int index;
		
		// sparse layer
		if (input.hasSparseVector())
		{
			weights = weight_vector.getSparseWeightVector();
			
			for (SparseItem p : input.getSparseVector())
			{
				for (int y=0; y<gradients.length; y++)
				{
					if(sampled_thinned_network[0][p.getIndex()] && sampled_thinned_network[1][1 + y]){
						index = weights.indexOf(y, p.getIndex());
						weights.add(index, gradients[y] * p.getValue());
					}
				}
			}
		}
		
		if (input.hasDenseVector())
		{
			weights = weight_vector.getDenseWeightVector();
			float[] x = input.getDenseVector();
			
			for (int y=0; y<gradients.length; y++)
			{
				for (int xi=0; xi<x.length; xi++)
				{
					if(sampled_thinned_network[0][input.getSparseVector().maxIndex() + 1 + xi] && sampled_thinned_network[1][1 + y]){
						index = weights.indexOf(y, xi);
						weights.add(index, -1 * getLearningRate(index, false) * gradients[y] * x[xi]);
					}
				}
			}
		}
	}
}
