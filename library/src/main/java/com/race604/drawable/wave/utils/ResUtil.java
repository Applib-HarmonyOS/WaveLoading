/*
 * Copyright (c) 2020 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.race604.drawable.wave.utils;

import com.race604.drawable.wave.LogUtil;
import ohos.agp.components.element.Element;
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
    public static PixelMap createByResourceId(Context context, int resourceId) {
        if (context == null) {
            return null;
        }

        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return null;
        }

        ohos.global.resource.Resource resource = null;
        try {
            resource = manager.getResource(resourceId);
        } catch (IOException | NotExistException e) {
            e.printStackTrace();
        }
        if (resource == null) {
            return null;
        }

        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
        srcOpts.formatHint = "image/png";
        ImageSource imageSource = null;
        try {
            imageSource = ImageSource.create(readResource(resource), srcOpts);
        } finally {
            close(resource);
        }
        if (imageSource == null) {
            return null;
        }
        ImageSource.DecodingOptions decodingOpts = new ImageSource.DecodingOptions();
        decodingOpts.desiredSize = new Size(0, 0);
        decodingOpts.desiredRegion = new Rect(0, 0, 0, 0);
        decodingOpts.desiredPixelFormat = PixelFormat.ARGB_8888;
        PixelMap pixelmap = imageSource.createPixelmap(decodingOpts);
        return pixelmap;
    }

    public static Element getPixelElementByResId(Context context, int resourceId) {
        return new PixelMapElement(createByResourceId(context, resourceId)).getCurrentElement();
    }

    public static PixelMapElement getPixelMapElementByResId(Context context, int resourceId) {
        return new PixelMapElement(createByResourceId(context, resourceId));
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
                break;
            }
        }
        return output.toByteArray();
    }

    private static void close(ohos.global.resource.Resource resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                LogUtil.info("Resouce", "Exception");
            }
        }
    }
}
