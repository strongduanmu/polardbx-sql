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

package org.apache.calcite.sql;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import java.util.LinkedList;
import java.util.List;

/**
 * @author youtianyu
 */
public class SqlShowHotkey extends SqlShow {

    private SqlSpecialOperator operator;

    private boolean full;

    private SqlNode table;

    private SqlNode partition;

    public SqlShowHotkey(SqlParserPos pos, List<SqlSpecialIdentifier> specialIdentifiers,
                         List<SqlNode> operands, SqlNode like, SqlNode where, SqlNode orderBy,
                         SqlNode limit, SqlNode table, SqlNode partition) {
        super(pos, specialIdentifiers, operands, like, where, orderBy, limit,
            specialIdentifiers.size() + operands.size() - 1);
        this.table = table;
        this.partition = partition;
    }

    public SqlNode getPartition(){
        return this.partition;
    }

    public SqlNode getTable(){
        return this.table;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    @Override
    protected boolean showWhere() {
        return false;
    }

    @Override
    public SqlOperator getOperator() {
        if (null == operator) {
            operator = new SqlShowHotkeyOperator(full);
        }
        return operator;
    }

    @Override
    public SqlKind getShowKind() {
        return SqlKind.SHOW_HOTKEY;
    }

    public static class SqlShowHotkeyOperator extends SqlSpecialOperator {
        private boolean full;

        public SqlShowHotkeyOperator(boolean full) {
            super("SHOW_HOTKEY", SqlKind.SHOW_HOTKEY);
            this.full = full;
        }

        @Override
        public RelDataType deriveType(SqlValidator validator, SqlValidatorScope scope, SqlCall call) {
            final RelDataTypeFactory typeFactory = validator.getTypeFactory();

            List<RelDataTypeFieldImpl> columns = new LinkedList<>();
            columns.add(new RelDataTypeFieldImpl("ID", 0, typeFactory.createSqlType(SqlTypeName.INTEGER)));
            columns.add(new RelDataTypeFieldImpl("GROUP_NAME", 1, typeFactory.createSqlType(SqlTypeName.CHAR)));
            columns.add(new RelDataTypeFieldImpl("TABLE_NAME", 2, typeFactory.createSqlType(SqlTypeName.CHAR)));
            columns.add(new RelDataTypeFieldImpl("SIZE_IN_MB", 3, typeFactory.createSqlType(SqlTypeName.DOUBLE)));

            return typeFactory.createStructType(columns);
        }
    }
}
