package cn.neil.netty.customprotocol;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author neil
 * @date 2020-02-16
 **/

@Data
@Accessors(chain = true)
public class CustProto {
    private long length;
    private String content;
}
