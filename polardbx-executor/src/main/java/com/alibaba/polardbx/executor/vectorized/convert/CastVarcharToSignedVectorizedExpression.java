/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.executor.vectorized.convert;

import com.alibaba.polardbx.common.utils.time.parser.StringNumericParser;
import com.alibaba.polardbx.executor.chunk.LongBlock;
import com.alibaba.polardbx.executor.chunk.MutableChunk;
import com.alibaba.polardbx.executor.chunk.RandomAccessBlock;
import com.alibaba.polardbx.executor.vectorized.AbstractVectorizedExpression;
import com.alibaba.polardbx.executor.vectorized.EvaluationContext;
import com.alibaba.polardbx.executor.vectorized.VectorizedExpression;
import com.alibaba.polardbx.executor.vectorized.VectorizedExpressionUtils;
import com.alibaba.polardbx.executor.vectorized.metadata.ExpressionSignatures;
import com.alibaba.polardbx.optimizer.core.datatype.DataType;
import io.airlift.slice.Slice;

import static com.alibaba.polardbx.executor.vectorized.metadata.ArgumentKind.Variable;

@SuppressWarnings("unused")
@ExpressionSignatures(names = {"CastToSigned", "ConvertToSigned"}, argumentTypes = {"Varchar"},
    argumentKinds = {Variable})
public class CastVarcharToSignedVectorizedExpression extends AbstractVectorizedExpression {

    public CastVarcharToSignedVectorizedExpression(DataType<?> outputDataType, int outputIndex,
                                                   VectorizedExpression[] children) {
        super(outputDataType, outputIndex, children);
    }

    @Override
    public void eval(EvaluationContext ctx) {
        super.evalChildren(ctx);

        MutableChunk chunk = ctx.getPreAllocatedChunk();
        int batchSize = chunk.batchSize();
        boolean isSelectionInUse = chunk.isSelectionInUse();
        int[] sel = chunk.selection();

        RandomAccessBlock outputVectorSlot = chunk.slotIn(outputIndex, outputDataType);
        RandomAccessBlock inputVectorSlot = chunk.slotIn(children[0].getOutputIndex(), children[0].getOutputDataType());

        long[] output = (outputVectorSlot.cast(LongBlock.class)).longArray();

        // handle nulls
        VectorizedExpressionUtils.mergeNulls(chunk, outputIndex, children[0].getOutputIndex());

        if (isSelectionInUse) {
            for (int i = 0; i < batchSize; i++) {
                int j = sel[i];

                // parse string value.
                Slice strValue = (Slice) inputVectorSlot.elementAt(j);
                output[j] = strValue == null ? 0
                    : StringNumericParser.parseString(strValue)[StringNumericParser.NUMERIC_INDEX];
            }
        } else {
            for (int i = 0; i < batchSize; i++) {
                // parse string value.
                Slice strValue = (Slice) inputVectorSlot.elementAt(i);
                output[i] = strValue == null ? 0
                    : StringNumericParser.parseString(strValue)[StringNumericParser.NUMERIC_INDEX];
            }
        }
    }
}
