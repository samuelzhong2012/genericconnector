/*
   Copyright 2015 Ant Kutschera

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package ch.maxant.generic_jca_adapter.demo;

import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.maxant.generic_jca_adapter.BasicTransactionAssistanceFactory;
import ch.maxant.generic_jca_adapter.BasicTransactionAssistanceFactoryImpl;

@Configuration
public class Config {

    @Bean(name="xa/bookingService")
    public BasicTransactionAssistanceFactory bookingServiceFactory() throws NamingException {
        BasicTransactionAssistanceFactory microserviceFactory = new BasicTransactionAssistanceFactoryImpl("xa/bookingService");
        return microserviceFactory;
    }
    
    @Bean(name="xa/letterService")
    public BasicTransactionAssistanceFactory letterServiceFactory() throws NamingException {
    	BasicTransactionAssistanceFactory microserviceFactory = new BasicTransactionAssistanceFactoryImpl("xa/letterService");
    	return microserviceFactory;
    }
	
}
