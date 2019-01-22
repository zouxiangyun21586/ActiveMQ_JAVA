package com.yr.test;

import java.io.Serializable;

/**
 * 实体类一定要序列化
 * 
 * @author liucong
 *
 * @date 2017年8月10日
 */
@SuppressWarnings("serial")
public class User implements Serializable {
	private Integer id;
	private String name;
	private String addr;
	private String sex;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", addr=" + addr + ", sex=" + sex + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

}
