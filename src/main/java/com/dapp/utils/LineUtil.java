package com.dapp.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: SuXinSen
 * @date: 2019/4/10
 * @time: 14:33
 * describe:
 */
@Slf4j
public class LineUtil {

    public Begin begin;
    public End end;

    public static void lineBreak() {
        log.info("=========================================================================");
    }

    public static void
    lineError(Exception e) {
        log.info("=============== " +e.toString()+ " ===============");
    }

    public static class Begin {

        public static void Build () {
            log.info("================================== begin =======================================");
        }

    }

    public static class End {

        public static void Build () {
            log.info("==================================  end  ====================================");
        }

    }
}
