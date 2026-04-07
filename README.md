<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>


<!-- PROJECT SHIELDS -->
<!-- No Shields because it's private -->

# mori: capturing life, together

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/re-nanashi/mori">
    <img src="images/logo.png" alt="Logo" width="150" height="150">
  </a>

  <p align="center">
    A social media platform that encourages intentional living by combining a life expectancy countdown with collaborative bucket lists.
    <br />
    <a href="https://github.com/re-nanashi/mori/tree/main/docs"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/re-nanashi/mori">View Demo</a>
    &middot;
    <a href="https://github.com/re-nanashi/mori/issues/new?labels=bug&template=1-bug-report.md">Report Bug</a>
    &middot;
    <a href="https://github.com/re-nanashi/mori/issues/new?labels=enhancement&template=4-feature-request.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

**Mori** is a social media application designed to help people become more mindful of how limited their time is. The app uses a life expectancy countdown to remind users that life is finite and encourages them to create meaningful experiences with the people they care about.

Users can build personal and shared **bucket lists**, track experiences by posting photos or videos, and collaborate with friends to complete activities together. Mori also recommends places and activities nearby—such as cafés, gyms, or local experiences—when users need inspiration or help deciding what to do next.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With
#### Backend
[![Java][Java]][Java-url]
[![Spring Boot][Spring Boot]][SpringBoot-url]
[![Spring Security][SpringSecurity]][SpringSecurity-url]
[![PostgreSQL][Postgres]][Postgres-url]

#### Mobile
[![Swift][Swift]][Swift-url]
[![SwiftUI][SwiftUI]][SwiftUI-url]

#### DevOps & Tools
[![Docker][Docker]][Docker-url]
[![Git][Git]][Git-url]
[![Postman][Postman]][Postman-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Follow these instructions to set up and run the Mori backend locally.

### Prerequisites

Make sure the following tools are installed on your machine:

* Java 21 or higher
* Apache Maven 4.0+
* Docker (_optional, for running services like PostgreSQL_)
* Git

### Installation

#### 1. Clone the repository
   ```sh
   git clone https://github.com/re-nanashi/mori.git
   ```
#### 2. Configure Environment Variables
   ```env
   # Database Config
   POSTGRES_USER=
   POSTGRES_PASSWORD=
   POSTGRES_DB=
   POSTGRES_HOST=localhost
   POSTGRES_PORT=5432

   # JDBC / Connection URL (for Spring Boot)
   DATABASE_URL=jdbc:postgresql://localhost:5432/mori

   # Spring Security Configuration
   NOTIF_ADMIN_USERNAME=
   NOTIF_ADMIN_PASSWORD=

   # JWT Security Configuration
   JWT_SECRET=
   JWT_ACCESS_TOKEN_EXPIRATION=
   JWT_REFRESH_TOKEN_EXPIRATION=

   # Admin account config
   ADMIN_EMAIL=
   ADMIN_PASSWORD=

   # Manager account config
   MANAGER_EMAIL=
   MANAGER_PASSWORD=

   # Email account for notifications (e.g., verification email)
   NOTIFICATION_EMAIL=
   NOTIFICATION_PASSWORD=
   ```
#### 3. Build the Project
   ```sh
   mvn clean install
   ```
#### 4. Run the application
   ```sh
   mvn spring-boot:run
   ```
   The API will start at:
   ```sh
   http://localhost:8080/api/v1
   ```
#### 5. Running with Docker (**Optional**)
   If using Docker for the database:
   ```sh
   docker-compose up -d
   ```
Then start the Spring Boot application normally.
#### 6. Verify the application (_Postman, cURL, frontend client_)
   ```sh
   curl http://localhost:8080/actuator/health
   ```
   
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ROADMAP -->
## Roadmap

- [X] User Management System
- [X] Custom Authentication and Authorization
- [ ] Core MVP Module
    - [ ] User Life Expectancy Dashboard
    - [ ] Bucket Lists (Individual, Team)
    - [ ] Social Graph (friends)
    - [ ] Memories Feed
    - [ ] Location-based queries

See the [open issues](https://github.com/re-nanashi/mori/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Top contributors:

<a href="https://github.com/re-nanashi/mori/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=re-nanashi/mori" alt="contrib.rocks image" />
</a>


<!-- CONTACT -->
## Contact

John Fabro - johnfabro7@gmail.com

Project Link: [https://github.com/re-nanashi/mori](https://github.com/re-nanashi/mori)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [Best-README-Template](https://github.com/othneildrew/Best-README-Template)
* [Canva Premium for creatives](https://canva.com)
* [Font Awesome](https://fontawesome.com)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/re-nanashi/mori.svg?style=for-the-badge
[contributors-url]: https://github.com/re-nanashi/mori/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/re-nanashi/mori.svg?style=for-the-badge
[forks-url]: https://github.com/re-nanashi/mori/network/members
[stars-shield]: https://img.shields.io/github/stars/re-nanashi/mori.svg?style=for-the-badge
[stars-url]: https://github.com/re-nanashi/mori/stargazers
[issues-shield]: https://img.shields.io/github/issues/re-nanashi/mori.svg?style=for-the-badge
[issues-url]: https://github.com/re-nanashi/mori/issues
[license-shield]: https://img.shields.io/github/license/re-nanashi/mori.svg?style=for-the-badge
[license-url]: https://github.com/re-nanashi/mori/blob/main/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/john-reymar-fabro-3b1125251/
[product-screenshot]: images/placeholder.png
<!-- Shields.io badges. You can a comprehensive list with many more badges at: https://github.com/inttter/md-badges -->
[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.java.com/
[Spring Boot]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[SpringSecurity]: https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white
[SpringSecurity-url]: https://spring.io/projects/spring-security
[Postgres]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[Postgres-url]: https://www.postgresql.org/
[Hibernate]: https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white
[Hibernate-url]: https://hibernate.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://react.dev/
[Next.js]: https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[Docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Git]: https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white
[Git-url]: https://git-scm.com/
[Postman]: https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white
[Postman-url]: https://www.postman.com/
[Swift]: https://img.shields.io/badge/Swift-F05138?style=for-the-badge&logo=swift&logoColor=white
[Swift-url]: https://developer.apple.com/swift/
[SwiftUI]: https://img.shields.io/badge/SwiftUI-0A84FF?style=for-the-badge&logo=swift&logoColor=white
[SwiftUI-url]: https://developer.apple.com/xcode/swiftui/