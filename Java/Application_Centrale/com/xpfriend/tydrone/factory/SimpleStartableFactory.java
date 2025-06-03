package com.xpfriend.tydrone.factory;

import com.xpfriend.tydrone.core.Logger;
import com.xpfriend.tydrone.core.OutputStreamFactory;
import com.xpfriend.tydrone.core.Startable;
import com.xpfriend.tydrone.telloio.*;

import Interfaces.IHM_ControleDrone;
import Threads.ThreadGestionTraitementImage;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;


public class SimpleStartableFactory {
    private static final SimpleStartableFactory instance = new SimpleStartableFactory(new SimpleLogger(), new SimpleOutputStreamFactory());

    private final Logger logger;
    private final OutputStreamFactory outputStreamFactory;

    private SimpleStartableFactory(Logger logger, OutputStreamFactory outputStreamFactory) {
        this.logger = logger;
        this.outputStreamFactory = outputStreamFactory;
    }

    public static SimpleStartableFactory getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public List<Startable> createStartables(String _Inet4Address, int IndiceDrone, IHM_ControleDrone _IHM_Drone, ThreadGestionTraitementImage _ThreadGestionTraitementImage) throws IOException, InterruptedException {
        MessageHandlerManager handlerManager = new MessageHandlerManager(outputStreamFactory, logger,_Inet4Address,IndiceDrone);
        DatagramChannel channel = handlerManager.connect();
        List<Startable> startables = new ArrayList<>();
        startables.add(new ChannelReceiver(handlerManager));
        startables.add(new ChannelRequester(handlerManager));
        startables.add(new TimerJobScheduler(handlerManager));
        startables.add(new VideoForwarder(channel,IndiceDrone,_Inet4Address));
        startables.add(new FFmpegVideoReceiver(IndiceDrone,_ThreadGestionTraitementImage));
        //startables.add(new FFmpegVideoRecorder(outputStreamFactory));
        return startables;
    }
}
