/*
 * Copyright 2011-2015 the original author or authors.
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
package midas.domain;

import java.io.Serializable;

/**
 * @author caio.amaral
 *
 */
public class CustomerDuplicatesIndexingPage implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer page;
	private Integer pages;
	private Integer count;

	public CustomerDuplicatesIndexingPage() {
	}

	public CustomerDuplicatesIndexingPage(final Integer page,
			final Integer pages, final Integer count) {
		this.page = page;
		this.pages = pages;
		this.count = count;
	}

	/**
	 * @return the page
	 */
	public Integer getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * @return the pages
	 */
	public Integer getPages() {
		return pages;
	}

	/**
	 * @param pages
	 *            the pages to set
	 */
	public void setPages(Integer pages) {
		this.pages = pages;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "IndexPage [page=" + page + ", pages=" + pages + ", count="
				+ count + "]";
	}
}
