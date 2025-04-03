package com.origami.sgm.services.ejbs;

import org.hibernate.Session;
import org.hibernate.Transaction;

import util.HiberUtil;

public abstract class SgmEEService {
	
	protected Session sess(){
		return HiberUtil.getSession();
	}
	
	protected Transaction requireTx(){
		return HiberUtil.requireTransaction();
	}
	
}
