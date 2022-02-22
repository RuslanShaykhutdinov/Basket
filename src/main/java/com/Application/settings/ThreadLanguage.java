package com.Application.settings;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThreadLanguage {
    static private final Map<Long, String> threadId2Lang = new HashMap<>();

    // запомнит язык для текущего потока
    public static synchronized void setLang(String lang)
    {
        if (lang != null)
            threadId2Lang.put(Thread.currentThread().getId(), lang.toLowerCase());
    }

    public static synchronized String getLang()
    {
        String lang = threadId2Lang.get( Thread.currentThread().getId() );
        if (lang != null)
            return lang.toLowerCase();
        return null;
    }

}
