package io.onedev.k8shelper;

import java.io.File;

import javax.annotation.Nullable;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.commons.utils.FileUtils;
import io.onedev.commons.utils.command.Commandline;

public class CheckoutFacade extends LeafFacade {

	private static final long serialVersionUID = 1L;

	private final int cloneDepth;
	
	private final boolean withLfs;
	
	private final boolean withSubmodules;
	
	private final CloneInfo cloneInfo;
	
	private final String checkoutPath;
	
	public CheckoutFacade(int cloneDepth, boolean withLfs, boolean withSubmodules, 
			CloneInfo cloneInfo, @Nullable String checkoutPath) {
		this.cloneDepth = cloneDepth;
		this.withLfs = withLfs;
		this.withSubmodules = withSubmodules;
		this.cloneInfo = cloneInfo;
		this.checkoutPath = checkoutPath;
	}

	public boolean isWithLfs() {
		return withLfs;
	}

	public boolean isWithSubmodules() {
		return withSubmodules;
	}

	public int getCloneDepth() {
		return cloneDepth;
	}

	public CloneInfo getCloneInfo() {
		return cloneInfo;
	}

	public String getCheckoutPath() {
		return checkoutPath;
	}

	public void setupWorkingDir(Commandline git, File workspace) {
		if (getCheckoutPath() != null) {
			if (getCheckoutPath().contains(".."))
				throw new ExplicitException("Checkout path should not contain '..'");
			git.workingDir(new File(workspace, getCheckoutPath()));
			FileUtils.createDir(git.workingDir());
		} else {
			git.workingDir(workspace);
		}
	}
	
}
