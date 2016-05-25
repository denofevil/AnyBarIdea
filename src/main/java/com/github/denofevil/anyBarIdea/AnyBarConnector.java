package com.github.denofevil.anyBarIdea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.AppIconScheme;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.ui.AppIcon;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Dennis.Ushakov
 */
public class AnyBarConnector implements ApplicationComponent {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1738;

    private static void setImage(AnyBarImage image, String hostName, int port) {
        try {
            final InetAddress host = InetAddress.getByName(hostName);
            final DatagramSocket socket = new DatagramSocket();
            byte[] data = image.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            Logger.getInstance(AnyBarConnector.class).error(e);
        }
    }

    @Override
    public void initComponent() {
        final AnyBarIcon anyBarIcon = new AnyBarIcon(AppIcon.getInstance());
        ReflectionUtil.setField(AppIcon.class, null, AppIcon.class, "ourIcon", anyBarIcon);
        ReflectionUtil.setField(AppIcon.class, null, AppIcon.class, "ourMacImpl", anyBarIcon);
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "AnyBarConnector";
    }

    private int getPort() {
        return DEFAULT_PORT;
    }

    private String getHost() {
        return DEFAULT_HOST;
    }

    private enum AnyBarImage {
        WHITE,
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        CYAN,
        BLUE,
        PURPLE,
        BLACK,
        QUESTION,
        EXCLAMATION;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private class AnyBarIcon extends AppIcon {
        private AppIcon delegate;

        AnyBarIcon(AppIcon icon) {
            delegate = icon;
        }

        @Override
        public boolean setProgress(Project project, Object o, AppIconScheme.Progress progress, double v, boolean isOk) {
            AnyBarConnector.setImage(AnyBarImage.YELLOW, getHost(), getPort());
            return delegate.setProgress(project, o, progress, v, isOk);
        }

        @Override
        public boolean hideProgress(Project project, Object o) {
            return delegate.hideProgress(project, o);
        }

        @Override
        public void setErrorBadge(Project project, String s) {
            AnyBarConnector.setImage(s != null ? AnyBarImage.EXCLAMATION : AnyBarImage.WHITE, getHost(), getPort());
            delegate.setErrorBadge(project, s);
        }

        @Override
        public void setOkBadge(Project project, boolean b) {
            AnyBarConnector.setImage(AnyBarImage.GREEN, getHost(), getPort());
            delegate.setOkBadge(project, b);
        }

        @Override
        public void requestAttention(Project project, boolean b) {
            delegate.requestAttention(project, b);
        }

        @Override
        public void requestFocus(IdeFrame ideFrame) {
            delegate.requestFocus(ideFrame);
        }
    }
}
