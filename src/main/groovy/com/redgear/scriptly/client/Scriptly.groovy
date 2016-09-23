package com.redgear.scriptly.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.redgear.scriptly.common.RequestMessage
import com.redgear.scriptly.server.ScriptlyServer
import com.redgear.scriptly.server.config.Config
import com.redgear.scriptly.server.lang.*
import com.redgear.scriptly.server.repo.Repository
import com.redgear.scriptly.server.repo.impl.AetherRepo

/**
 * Created by LordBlackHole on 9/2/2016.
 */
class Scriptly {


    public static void main(String[] args) {

        if (args.length < 2) {
            throw new Exception("Expecting at least two arguments, language and source file")
        }


        def message = new RequestMessage(lang: args[0], source: args[1], args: args.toList().subList(2, args.length))


        if(!tryCall(message)) {

            'java -jar C:\\Users\\ft4\\Projects\\Scriptly\\build\\libs\\scriptly-0.1.0-SNAPSHOT.jar -main com.redgear.scriptly.server.ScriptlyServer'.execute()

            tryCall(message)
        }

    }


    private static boolean tryCall(RequestMessage message) {
        try {

            def con = 'http://localhost:8555/'.toURL().openConnection()

            con.doOutput = true
            con.doInput = true

            con.setRequestProperty("Content-Type", "application/json; charset=utf8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Method", "POST");

            con.outputStream.withWriter {

                new ObjectMapper().writeValue(it, message)

            }

            println con.inputStream.text

            return true
        } catch (Exception e) {
            e.printStackTrace()
            return false
        }
    }




}
