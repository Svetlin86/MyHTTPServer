package com.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

         /*---DEFINING CONSTANTS ---*/
    private static final int SP = 0x20; // 32 <-- once detected the prior message gets parsed (and if any errors detected )
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException { //  <---- method to parse HTTP request
        InputStreamReader reader = new InputStreamReader(inputStream , StandardCharsets.US_ASCII);//<-- Bridge
                                                                        // from a byte stream to a char stream
                                                          // reads bytes and decodes it in to a specific charset
        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseHeaders(reader, request);
        parseBody(reader, request);

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {

        StringBuilder processingDataBuffer = new StringBuilder();

        /*--differentiates each line of the debugger to identify each of the items we are looking at--*/
        boolean methodParsed = false;
        boolean requestTargetParsed =false;


        int _byte;
        while ((_byte = reader.read()) >=0){
            if (_byte == CR){
                _byte = reader.read();
                if (_byte == LF){
                    LOGGER.debug("Request Line VERSION to Process : {} ", processingDataBuffer.toString());//debugger line
                    if (!methodParsed || !requestTargetParsed){
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    try {
                        request.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                }else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }

            if (_byte == SP){
                //TODO Process previous data
                if (!methodParsed){
                    LOGGER.debug("Request Line to METHOD Process : {} ", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                }else if(!requestTargetParsed){
                    LOGGER.debug("Request Line to REQUEST TARGET Process : {} ", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());
            }else{
                processingDataBuffer.append((char)_byte);
                if(!methodParsed){
                    if(processingDataBuffer.length() > HttpMethod.MAX_LENGTH){
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) {

    }

    private void parseBody(InputStreamReader reader, HttpRequest request) {

    }






}
