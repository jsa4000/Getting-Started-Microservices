# Asymmetric KeyPair

## Authentication Server

- Create **private** and **public** keys.

    > Make sure ``keypass`` and ``storepass`` are the same.

        keytool -genkeypair -alias sign-key
                        -keyalg RSA
                        -keypass password
                        -keystore sign-key.jks
                        -storepass password

- Copy the generated file into ``resources/sign-key.jks``
- Add following code to ``AuthorizationServerConfigurerAdapter``

    ```java
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SYMMETRIC_KEY);

        /*
        // Add key-pair signing method instead symmetric. See 'resources/README.md'
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("sign-key.jks"), "password".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("sign-key"));
         */

        return converter;
    }
    ```

## Resources Server

- Export public-key

        keytool -list -rfc --keystore sign-key.jks | openssl x509 -inform pem -pubkey

- Copy just the public key part into a file "resources/public-key.txt"

    ```txt
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIK2Wt4x2EtDl41C7vfp
    OsMquZMyOyteO2RsVeMLF/hXIeYvicKr0SQzVkodHEBCMiGXQDz5prijTq3RHPy2
    /5WJBCYq7yHgTLvspMy6sivXN7NdYE7I5pXo/KHk4nz+Fa6P3L8+L90E/3qwf6j3
    DKWnAgJFRY8AbSYXt1d5ELiIG1/gEqzC0fZmNhhfrBtxwWXrlpUDT0Kfvf0QVmPR
    xxCLXT+tEe1seWGEqeOLL5vXRLqmzZcBe1RZ9kQQm43+a9Qn5icSRnDfTAesQ3Cr
    lAWJKl2kcWU1HwJqw+dZRSZ1X4kEXNMyzPdPBbGmU6MHdhpywI7SKZT7mX4BDnUK
    eQIDAQAB
    -----END PUBLIC KEY-----
    ```txt

- Add following code to ``ResourceServerConfigurerAdapter``

  > Just for standalone and separated instance from **Authentication server**. **Resource Server** must verify the token by itself using the same **signing method** as Authentication Server or use the ``/oauth/check_token`` API endpoint on Authentication server. The first option is the best one for this case.

    ```java
    @Configuration
    @EnableResourceServer
    public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer config) {
            config.tokenServices(tokenServices());
        }

        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(accessTokenConverter());
        }

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey("123");

            /*
            // If Authentication Server is using asymmetric key-pair for signing the token.
            Resource resource = new ClassPathResource("public-key.txt");
            String publicKey = null;
            try {
                publicKey = IOUtils.toString(resource.getInputStream());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            converter.setVerifierKey(publicKey);
            */

            return converter;
        }

        @Bean
        @Primary
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenStore(tokenStore());
            return defaultTokenServices;
        }
    }
    ```