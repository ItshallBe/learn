package com.zzx.atom;

import com.zzx.IAtom;
import com.zzx.model.BizContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Atom3 implements IAtom {
    @Override
    public void exec(BizContent bizContent) {
        log.info("Atom3 exec");
    }
}
