package com.fsk.framework.gray.client.service;

import com.fsk.framework.gray.bean.RegistryParam;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/12/9
 * @Describe: Client.
 */
public interface Client {
    void register(RegistryParam param) throws Exception;
    void offline(RegistryParam param) throws Exception;
}
