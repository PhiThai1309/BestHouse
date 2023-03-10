# Best House Application

<p align="center">
  <img height=500 src="https://user-images.githubusercontent.com/71892904/215958698-a4cc8cbe-97f8-492d-bc68-3b73fa2ff820.png"/>
  
  <img height=500 src="https://user-images.githubusercontent.com/71892904/215959761-b5db94d4-3f34-49df-ba2b-9f51b9ebb639.png"/>
  
  <img height=500 src="https://user-images.githubusercontent.com/71892904/215960228-2474fd07-2e9a-48bb-a540-a34ab152beec.png"/>

</p>

## Table of contents

- [Overview](#overview)
- [Functionalities](#Functionalities)
- [Built with](#built-with)
- [What our team learned](#what-our-team-learned)
- [Continued development](#continued-development)
- [Author](#author)
- [Acknowledgments](#acknowledgments)


## Overview

This project is about building an e-commerce application that helps users easily find and rent properties. The app supports 2 main actors: Landlords can create properties and post them online, while Tenants can review those properties, ask for a rental contract and chat with the property owner to know more about the property. The application lets landlords chat with multiple people wanting to rent the property and accept the contract where they prefer the most.

### Functionalities

Users should be able to:
- Create tenant or landlord account if they are new to the system
- Login using the application's account or via Facebook or Google account
- All users will be logout of the application if they are inactive for 5 minutes
- View all contract history that they are associated with
- The Landlord can post their property for rent by inputting the building name, type (Apartment, House, etc.), address, necessities, pictures of their house, and a detailed description of their property.
- The Tenant can search for suitable properties by address, which can be a place or nearby location in a map.
- The Tenant can initiate the renting process by making a contract with the landlord (The contract duration is set for 1 year)
- after making a contract, both Tenant and Landlord can chat with each other about that property.
- The Landlord can receive multiple pending requests for a single available property, and after accepting a contract for 1 tenant, all other pending requests from other tenants will automatically be rejected.
- creating a contract will notify the landlord of a new contract, whereas accepting or rejecting a contract for a property will notify all tenant with a current pending contract to that property.

### Built with

<p align="center">
  <img src="https://skillicons.dev/icons?i=androidstudio" />
  <img src="https://skillicons.dev/icons?i=firebase">
  <img src="https://skillicons.dev/icons?i=figma">
</p>

- Android Studio is the official IDE is used to develop our android application.
- Firebase
- Github & Git
- Google Map API
- Place API
- Figma
- Material Design Framework
- Microsoft Visio

### What our team learned
Through this project, our team has learned how to build a multi-functional Android application that consists of many features including Google Map API, Google Firestore, chat feature, and many more. Most and foremost, we have learned how to work as a teammate, collaborate using GitHub and Git, and handle conflict if there is a merge issue. Although this project only lasts for 1 month, we have accomplished many things as a team and as a result, we are proud to deliver Best House application.

### Continued development
We intend to provide more quality-of-life improvements to the application and to further improve the main functionality of our app. One of the missing features of our app is to provide a payment method to our app, which can be from direct payment through a wallet application such as Momo or a debit banking account. Also, the main dashboard for tenants could have more customized search functions for filtering and searching for specific properties. An Administrator actor could be very useful for Tenants to verify the posted property from Landlords, so Tenants can be assured that their renting building is up to quality.


## Author
- Nguyen Nam Cuong (s3891758): 25%, Authentication function, FireStore handle, Image handle
- Nguyen Ngoc Minh (s3907086): 25%, Service, Broadcast, Notification
- Nguyen Vu Minh Duy (s3878076): 25% Map API, Chat
- Thai Manh Phi (s3878070): 25% UI design, Frontend developer, FireStore handle

Total contribution: 100%

## Acknowledgments
Details in code files
