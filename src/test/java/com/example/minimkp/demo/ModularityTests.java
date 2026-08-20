package com.example.minimkp.demo;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(DemoApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
