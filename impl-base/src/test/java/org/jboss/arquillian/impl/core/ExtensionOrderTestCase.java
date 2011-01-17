/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.impl.core;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.impl.core.spi.Manager;
import org.jboss.arquillian.spi.core.Instance;
import org.jboss.arquillian.spi.core.InstanceProducer;
import org.jboss.arquillian.spi.core.annotation.ApplicationScoped;
import org.jboss.arquillian.spi.core.annotation.Inject;
import org.jboss.arquillian.spi.core.annotation.Observes;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * ExtensionOrderTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Ignore // Ordering not implemented
public class ExtensionOrderTestCase
{
   private static List<String> callOrder = new ArrayList<String>();

   @Test
   public void shouldExecuteProducersOnSameEventBeforeConsumers() throws Exception
   {
      Manager manager = ManagerBuilder.from()
                  .extensions(ConsumerOne.class, ProducerOne.class).create();
      
      manager.fire("test");
      
      Assert.assertEquals("ProducerOne", callOrder.get(0));
      Assert.assertEquals("ProducerTwo", callOrder.get(1));
      Assert.assertEquals("ConsumerOne", callOrder.get(2));
   }
   
   public static class ProducerOne 
   {
      @Inject @ApplicationScoped
      private InstanceProducer<ValueOne> value;
      
      public void exec(@Observes String ba) 
      { 
         callOrder.add(this.getClass().getSimpleName());
         value.set(new ValueOne());
      }
   }
   
   public static class ProducerTwo
   {
      @Inject @ApplicationScoped
      private InstanceProducer<ValueTwo> value;
      
      public void exec(@Observes String ba) 
      { 
         callOrder.add(this.getClass().getSimpleName());
         value.set(new ValueTwo());
      }
   }

   public static class ConsumerOne
   {
      @Inject
      private Instance<ValueOne> value;
      
      public void exec(@Observes String ba) 
      { 
         callOrder.add(this.getClass().getSimpleName());
         System.out.println(value.get());
      }
   }
   
   private static class ValueOne { }
   private static class ValueTwo { }
}