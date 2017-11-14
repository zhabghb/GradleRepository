package com.cetc.hubble.metagrid.elasticsearch;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;

/**
 * Created by dahey on 2017/4/13.
 */
public class EmbedNode extends org.elasticsearch.node.Node{
    public EmbedNode(Settings preparedSettings, Collection<Class<? extends Plugin>> classpathPlugins) {
        super(InternalSettingsPreparer.prepareEnvironment(preparedSettings, null), classpathPlugins);
    }

}
