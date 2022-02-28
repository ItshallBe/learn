package com.zzx;

import com.zzx.atom.Atom1;
import com.zzx.atom.Atom2;
import com.zzx.atom.Atom3;
import com.zzx.model.BizContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@ContextConfiguration(locations = {"classpath:test.xml"})
public class AtomTest {
    @Resource
    Atom1 atom1;
    @Resource
    Atom2 atom2;
    @Resource
    Atom3 atom3;

    @Test
    public void test() {
        BizContent bizContent = new BizContent();
        if (atom1 == null) System.out.println("atom null");
        atom1.exec(bizContent);
        atom2.exec(bizContent);
        atom3.exec(bizContent);
    }
}
