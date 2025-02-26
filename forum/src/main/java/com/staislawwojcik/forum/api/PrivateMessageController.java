package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.request.PMRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/pm")
public class PrivateMessageController {

    @PostMapping(value = "/{receiverId}")
    void sendPM(@PathVariable String receiverId , @RequestHeader("token") String senderToken, @RequestBody PMRequest pmRequest){
        System.out.println(receiverId);
        System.out.println(senderToken);
        System.out.println(pmRequest);
    }
}
