/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package demo.oauth2.client.model;

import javax.persistence.Id;

/**
 * 基础信息
 */
public class BaseEntity  {
	@Id
	protected Integer modelId;
	
    public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
}
