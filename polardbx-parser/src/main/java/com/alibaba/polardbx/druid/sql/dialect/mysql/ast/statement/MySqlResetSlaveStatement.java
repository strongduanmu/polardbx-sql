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

package com.alibaba.polardbx.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.polardbx.druid.sql.ast.SQLStatementImpl;
import com.alibaba.polardbx.druid.sql.ast.SqlType;
import com.alibaba.polardbx.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.polardbx.druid.sql.visitor.SQLASTVisitor;

/**
 * @author mario
 */
public class MySqlResetSlaveStatement extends SQLStatementImpl {
    private boolean all;
    private SQLCharExpr channel;
    private SQLCharExpr subChannel;

    @Override
    protected void accept0(SQLASTVisitor v) {
        v.visit(this);
        v.endVisit(this);
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public SQLCharExpr getChannel() {
        return channel;
    }

    public void setChannel(SQLCharExpr channel) {
        this.channel = channel;
    }

    @Override
    public SqlType getSqlType() {
        return null;
    }

    public SQLCharExpr getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(SQLCharExpr subChannel) {
        this.subChannel = subChannel;
    }
}
