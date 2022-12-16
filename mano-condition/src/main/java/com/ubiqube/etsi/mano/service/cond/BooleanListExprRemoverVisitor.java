/**
 *     Copyright (C) 2019-2020 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.cond;

import java.util.List;

import com.ubiqube.etsi.mano.service.cond.ast.AttrHolderExpr;
import com.ubiqube.etsi.mano.service.cond.ast.BooleanExpression;
import com.ubiqube.etsi.mano.service.cond.ast.BooleanListExpr;
import com.ubiqube.etsi.mano.service.cond.ast.GenericCondition;
import com.ubiqube.etsi.mano.service.cond.ast.RangeValueExpr;
import com.ubiqube.etsi.mano.service.cond.ast.SizeOfExpr;

public class BooleanListExprRemoverVisitor extends SimpleNodeReturn<BooleanOperatorEnum> {

	@Override
	public Node visit(final BooleanListExpr booleanListExpr, final BooleanOperatorEnum arg) {
		final List<BooleanExpression> conds = booleanListExpr.getCondition();
		Node root = null;
		if (booleanListExpr.getOp() == BooleanOperatorEnum.NOT) {
			return removeNot(booleanListExpr, arg);
		}
		while (!conds.isEmpty()) {
			if (null == root) {
				root = conds.get(0).accept(this, booleanListExpr.getOp());
				conds.remove(0);
			}
			root = new GenericCondition(root, convert(booleanListExpr.getOp()), conds.get(0).accept(this, booleanListExpr.getOp()));
			conds.remove(0);
		}
		return root;
	}

	private Node removeNot(final BooleanListExpr booleanListExpr, final BooleanOperatorEnum arg) {
		final List<BooleanExpression> conds = booleanListExpr.getCondition();
		Node root = null;
		while (!conds.isEmpty()) {
			if (null == root) {
				final BooleanExpression cond = conds.get(0);
				invertOperator(cond);
				root = cond.accept(this, arg);
				conds.remove(0);
			}
			final BooleanExpression cond = conds.get(0);
			invertOperator(cond);
			root = new GenericCondition(root, convert(arg), cond.accept(this, arg));
			conds.remove(0);
		}
		return root;
	}

	private static void invertOperator(final BooleanExpression cond) {
		if (cond instanceof final GenericCondition gc) {
			gc.setOp(AstUtils.invert(gc.getOp()));
		} else if (cond instanceof final RangeValueExpr rve) {
			rve.setNot(true);
		} else {
			throw new AstException("Unable to handle " + cond.getClass().getSimpleName());
		}
	}

	private static Operator convert(final BooleanOperatorEnum op) {
		return switch (op) {
		case AND -> Operator.AND;
		case OR -> Operator.OR;
		case NOT -> Operator.NOT;
		case ASSERT -> Operator.ASSERT;
		default -> throw new IllegalArgumentException("Unexpected value: " + op);
		};
	}

	@Override
	public Node visit(final AttrHolderExpr expr, final BooleanOperatorEnum args) {
		final List<BooleanExpression> conds = expr.getConditions();
		Node root = null;
		while (!conds.isEmpty()) {
			if (null == root) {
				root = conds.get(0).accept(this, args);
				conds.remove(0);
			}
			root = new GenericCondition(root, convert(args), conds.get(0).accept(this, args));
			conds.remove(0);
		}
		return root;
	}

	@Override
	public Node visit(final SizeOfExpr expr, final BooleanOperatorEnum arg) {
		expr.getLeft().accept(this, arg);
		return expr;
	}

}
