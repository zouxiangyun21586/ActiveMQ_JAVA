package com.yr.zxy;

import java.io.Serializable;

/**
 * 需存入流就必须将实体类序列化
 * 
 * @author zxy
 *
 * 2018年6月7日 下午5:59:09
 *
 */
@SuppressWarnings("serial")
public class Zxy implements Serializable {
	private int age;
	private String name;
	private String addr;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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

	@Override
	public String toString() {
		return "Zxy [age=" + age + ", name=" + name + ", addr=" + addr + "]";
	}

}
