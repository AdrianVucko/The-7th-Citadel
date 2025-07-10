package com.tvz.avuckovic.the7thcitadel.jndi;

import com.tvz.avuckovic.the7thcitadel.utils.FileUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public class InitialDirContextCloseable extends InitialDirContext implements AutoCloseable {

    public InitialDirContextCloseable() throws NamingException {
        String configurationFileProviderUrl = "file:" + FileUtils.getDiskRoot();
        addToEnvironment(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
        addToEnvironment(Context.PROVIDER_URL, configurationFileProviderUrl);
    }
}
