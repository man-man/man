package com.app.common;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * JSONObject的序列化扩展，主要是bundler不支持直接对object对象的put，只支持序列化或另一个proxxx的方法，所以这里，
 * 进行序列化的扩展
 * 
 * @author samoin
 * 
 */
public class JSONObjectSerializalble extends JSONObject implements Serializable {

	private static final long serialVersionUID = 1L;

}
