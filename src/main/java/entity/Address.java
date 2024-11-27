package entity;

public class Address {

        private String streetAddress;
        private String city;
        private String country;

        public Address(String streetAddress, String city, String country) {
            this.streetAddress = streetAddress;
            this.city = city;
            this.country = country;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }



