package br.gov.lexml.util.hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SimpleHqlBuilder {
	
	private final String selectClause;
	private final List<String> whereOperators = new ArrayList<String>();
	private final List<String> whereClauses = new ArrayList<String>();
	private final List<Object> paramValues = new ArrayList<Object>();
	private String orderByClause;

	public SimpleHqlBuilder(final String selectClause) {
		this.selectClause = selectClause;
	}
	
	public void orderBy(final String orderByClause) {
		this.orderByClause = orderByClause;
	}
	
	public SimpleHqlBuilder and(final String whereClause, final Object... paramValues) {
		return addWhereClause(" and ", whereClause, paramValues);
	}
	
	public SimpleHqlBuilder or(final String whereClause, final Object... paramValues) {
		return addWhereClause(" or ", whereClause, paramValues);
	}
	
	private SimpleHqlBuilder addWhereClause(final String whereOperator, final String whereClause, final Object... paramValues) {
		whereClauses.add(whereClause);
		whereOperators.add(whereOperator);
		this.paramValues.addAll(Arrays.asList(paramValues));
		return this;
	}
	
	public String getHql() {
		StringBuilder where = new StringBuilder();
		if(!whereClauses.isEmpty()) {
			boolean first = true;
			int size = whereClauses.size();
			for(int i = 0; i < size; i++) {
				if(first) {
					where.append(" where ");
					first = false;
				}
				else {
					where.append(whereOperators.get(i));
				}
				where.append(whereClauses.get(i));
			}
		}
		return selectClause + where + 
			(orderByClause != null? " order by " + orderByClause: "");
	}
	
	public Object[] getParams() {
		return paramValues.toArray();
	}
}
