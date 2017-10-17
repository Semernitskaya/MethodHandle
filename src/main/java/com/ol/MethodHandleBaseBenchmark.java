/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ol;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@State(Scope.Benchmark)
public class MethodHandleBaseBenchmark {

    TestObject object;

    MethodHandle methodHandlePublic;

    Method reflectionMethodPublic;

    MethodHandle methodHandlePrivate;

    Method reflectionMethodPrivate;

    @Setup
    public void prepare() throws NoSuchMethodException, IllegalAccessException {
        object = new TestObject();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(void.class, String.class);

        reflectionMethodPublic = TestObject.class.getMethod("doSomethingPublic", String.class);
        methodHandlePublic = lookup.findVirtual(TestObject.class, "doSomethingPublic", methodType);

        reflectionMethodPrivate = TestObject.class.getDeclaredMethod("doSomethingPrivate", String.class);
        reflectionMethodPrivate.setAccessible(true);
        methodHandlePrivate = lookup.unreflect(reflectionMethodPrivate);
    }

    @Benchmark
    public void useInvokeDynamicPublic() throws Throwable {
        methodHandlePublic.invoke(object, "str");
    }

    @Benchmark
    public void useInvokePublic() {
        object.doSomethingPublic("str");
    }

    @Benchmark
    public void useReflectionPublic() throws InvocationTargetException, IllegalAccessException {
        reflectionMethodPublic.invoke(object, "str");
    }

    @Benchmark
    public void useInvokeDynamicPrivate() throws Throwable {
        methodHandlePrivate.invoke(object, "str");
    }

    @Benchmark
    public void useReflectionPrivate() throws InvocationTargetException, IllegalAccessException {
        reflectionMethodPrivate.invoke(object, "str");
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MethodHandleBaseBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(7)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
