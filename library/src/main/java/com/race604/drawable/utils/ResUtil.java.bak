package com.race604.drawable.utils;

import ohos.agp.components.element.PixelMapElement;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Rect;
import ohos.media.image.common.Size;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResUtil {
    public static PixelMap createByResourceId(Context context, int resourceid) {
        if (context != null) {
            ResourceManager manager = context.getResourceManager();
            if (manager != null) {
                ohos.global.resource.Resource resource = null;
                try {
                    resource = manager.getResource(resourceid);
                } catch (NotExistException | IOException e) {
                    e.printStackTrace();
                }
            if (resource != null) {
                ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
                srcOpts.formatHint = "image/png";
                ImageSource imageSource = null;

                try {
                    imageSource = ImageSource.create(readResource(resource), srcOpts);
                } finally {
                    close(resource);
                }
                if (imageSource != null) {
                    ImageSource.DecodingOptions decodingOpts = new ImageSource.DecodingOptions();
                    decodingOpts.desiredSize = new Size(0, 0);
                    decodingOpts.desiredRegion = new Rect(0, 0, 0, 0);
                    decodingOpts.desiredPixelFormat = PixelFormat.ARGB_8888;
                    PixelMap pixelMap = imageSource.createPixelmap(decodingOpts);
                    return pixelMap;
                }
            }
            }
        }
        return null;
    }

    public static PixelMapElement getPMEByResId (Context context, int resourceid) {
        return new PixelMapElement(createByResourceId(context, resourceid));
    }

    private static byte[] readResource(ohos.global.resource.Resource resource) {
        final int bufferSize = 1024;
        final int ioEnd = -1;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        while (true) {
            try {
                int readLen = resource.read(buffer, 0, bufferSize);
                if (readLen == ioEnd) {
                    break;
                }
                output.write(buffer, 0, readLen);
            } catch (IOException e) {
                LogUtil.info("Exception", e.getMessage());
            }
        }
        return output.toByteArray();
    }

    private static void close(ohos.global.resource.Resource resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}