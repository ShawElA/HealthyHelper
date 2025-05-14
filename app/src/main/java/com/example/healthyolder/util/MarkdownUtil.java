package com.example.healthyolder.util;

import android.content.Context;
import android.widget.TextView;

import io.noties.markwon.Markwon;

/**
 * Markdown渲染工具类
 */
public class MarkdownUtil {
    
    private static Markwon markwonInstance;
    
    /**
     * 获取Markwon实例
     * @param context 上下文
     * @return Markwon实例
     */
    public static Markwon getMarkwon(Context context) {
        if (markwonInstance == null) {
            markwonInstance = Markwon.create(context);
        }
        return markwonInstance;
    }
    
    /**
     * 将Markdown文本渲染到TextView
     * @param context 上下文
     * @param textView 目标TextView
     * @param markdown Markdown文本
     */
    public static void setMarkdown(Context context, TextView textView, String markdown) {
        if (markdown == null || textView == null) {
            return;
        }
        
        getMarkwon(context).setMarkdown(textView, markdown);
    }
} 