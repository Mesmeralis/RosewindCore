package me.vividheart.core;

import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelPlugin;

public class PointsSentinelIntegration extends SentinelIntegration {
    private final SentinelPlugin sentinel = SentinelPlugin.getPlugin(SentinelPlugin.class);

    public PointsSentinelIntegration(){
        this.sentinel.registerIntegration(this);
    }


}
