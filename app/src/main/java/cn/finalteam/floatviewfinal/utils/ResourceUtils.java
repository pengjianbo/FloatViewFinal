/*
 * Copyright (C) 2015 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.floatviewfinal.utils;

import android.content.Context;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午9:01
 */
public class ResourceUtils {
    /**
     * 获取 layout 布局文件
     * @param context Context
     * @param resName  layout xml 的文件名
     * @return layout
     */
    public static int getLayoutId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "layout",
                context.getPackageName());
    }

    /**
     * 获取 string 值 
     * @param context  Context
     * @param resName   string name的名称
     * @return string
     */
    public static int getStringId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string",
                context.getPackageName());
    }

    /**
     * 获取 drawable 布局文件 或者 图片的 
     * @param context  Context
     * @param resName drawable 的名称
     * @return drawable
     */
    public static int getDrawableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "drawable", context.getPackageName());
    }


    /**
     * 获取 style 
     * @param context Context 
     * @param resName  style的名称
     * @return style
     */
    public static int getStyleId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "style",
                context.getPackageName());
    }

    /**
     * 获取 styleable
     * @param context  Context 
     * @param resName  styleable 的名称
     * @return styleable
     */
    public static Object getStyleableId(Context context, String resName){
        return context.getResources().getIdentifier(resName, "styleable",
                context.getPackageName());
    }


    /**
     * 获取 anim 
     * @param context  Context 
     * @param resName  anim xml 文件名称
     * @return anim
     */
    public static int getAnimId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "anim",
                context.getPackageName());
    }

    /**
     * 获取 id 
     * @param context Context
     * @param resName id 的名称
     * @return
     */
    public static int getId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "id",
                context.getPackageName());
    }

    /**
     * color
     * @param context  Context
     * @param resName  color 名称
     * @return
     */
    public static int getColorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "color",
                context.getPackageName());
    }
}
