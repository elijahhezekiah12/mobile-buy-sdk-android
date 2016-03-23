/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.shopify.buy.service;

import com.shopify.buy.dataprovider.BuyClient;
import com.shopify.buy.extensions.ShopifyAndroidTestCase;
import com.shopify.buy.model.Address;
import com.shopify.buy.model.Customer;
import com.shopify.buy.model.Order;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerTest extends ShopifyAndroidTestCase {

    private static final boolean ENABLED = false;

    private Customer customer;
    private List<Order> orders;
    private List<Address> addresses;
    private Address address;
    private String customerToken;

    public void testCustomerCreation() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final Customer customer = getCustomer();

        buyClient.createCustomer(customer, "password", new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                assertNotNull(customer);
                assertEquals(false, buyClient.getCustomerToken().isEmpty());
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testCustomerActivation() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        // TODO update this test when we start to get real tokens
        buyClient.activateCustomer(customer.getId(), "notanactivationtoken", "notapassword", new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                assertNotNull(customer);
                assertEquals(false,buyClient.getCustomerToken().isEmpty());
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }


    public void testCustomerLogin() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        customer = getCustomer();

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.loginCustomer(customer.getEmail(), "password", new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                assertNotNull(customer);
                assertEquals(false, buyClient.getCustomerToken().isEmpty());

                CustomerTest.this.customer = customer;
                CustomerTest.this.customerToken = buyClient.getCustomerToken();

                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testCustomerLogout() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.logoutCustomer(new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testCustomerRenew() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.renewCustomer(new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testCustomerRecover() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);

        Customer customer = getCustomer();

        buyClient.recoverCustomer("email", new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testCustomerUpdate() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);


        customer.setLastName("Foo");

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.updateCustomer(customer, new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                assertNotNull(customer);
                assertEquals("Foo", customer.getLastName());
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testGetCustomerOrders() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.getOrders(new Callback<List<Order>>() {
            @Override
            public void success(List<Order> orders, Response response) {
                assertNotNull(orders);
                assertEquals(true, orders.size() > 0);
                CustomerTest.this.orders = orders;
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }

        });
        latch.await();
    }

    public void testGetOrder() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testGetCustomerOrders();
        buyClient.setCustomerToken(customerToken);


        final CountDownLatch latch = new CountDownLatch(1);

        String orderId = orders.get(0).getOrderId();

        buyClient.getOrder(orderId, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                assertNotNull(order);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testGetCustomer() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.getCustomer(new Callback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                assertNotNull(customer);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setEmail("krisorr@gmail.com");
        customer.setFirstName("Kristopher");
        customer.setLastName("Orr");

        return customer;
    }

    public void testCreateAddress() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.createAddress(getAddress(), new Callback<Address>() {
            @Override
            public void success(Address address, Response response) {
                Address input = getAddress();
                assertEquals(input.getAddress1(), address.getAddress1());
                assertEquals(input.getAddress2(), address.getAddress2());
                assertEquals(input.getCity(), address.getCity());
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });
        latch.await();
    }

    public void testGetAddresses() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testCustomerLogin();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        buyClient.getAddresses(new Callback<List<Address>>() {
            @Override
            public void success(List<Address> addresses, Response response) {
                assertNotNull(addresses);
                assertEquals(true, addresses.size() > 0);
                CustomerTest.this.addresses = addresses;
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });

        latch.await();
    }

    public void testGetAddress() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testGetAddresses();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        String addressId = addresses.get(0).getAddressId();

        buyClient.getAddress(addressId, new Callback<Address>() {
            @Override
            public void success(Address address, Response response) {
                assertNotNull(address);
                CustomerTest.this.address = address;
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });

        latch.await();
    }

    public void testUpdateAddress() throws InterruptedException {
        if (!ENABLED) {
            return;
        }

        testGetAddress();
        buyClient.setCustomerToken(customerToken);

        final CountDownLatch latch = new CountDownLatch(1);

        address.setCity("Toledo");

        buyClient.updateAddress(address, new Callback<Address>() {
            @Override
            public void success(Address address, Response response) {
                assertNotNull(address);
            }

            @Override
            public void failure(RetrofitError error) {
                fail(BuyClient.getErrorBody(error));
            }
        });

        latch.await();
    }

    private Address getAddress() {
        Address shippingAddress = new Address();
        shippingAddress.setAddress1("150 Elgin Street");
        shippingAddress.setAddress2("8th Floor");
        shippingAddress.setCity("Ottawa");
        shippingAddress.setProvinceCode("ON");
        shippingAddress.setCompany("Shopify Inc.");
        shippingAddress.setFirstName("MobileBuy");
        shippingAddress.setLastName("TestBot");
        shippingAddress.setPhone("1-555-555-5555");
        shippingAddress.setCountryCode("CA");
        shippingAddress.setZip("K1N5T5");
        return shippingAddress;
    }
}