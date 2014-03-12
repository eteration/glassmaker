glassmaker
==========

Glassmaker helps you to build Java based Glassware for the Google Glass using Mirror API

Glassmaker has two primary parts:
  - Eclipse tools to simplify the process of authoring and creating Java based glassware using the Mirror API. 
  - And components for springframework to simplify the development of Java modules. These components include SSO using Google OAuth2 services, spring templates for the Mirror API, and Timeline Item templates (Card) using Freemarker.

The Glassmaker IDE tooling can be installed into an existing Eclipse IDE from

  - http://glassmaker.org/updates/0.0.1

For FAQ, and more information visit the site:

  - http://eteration.github.io/glassmaker/faq.html


Glassmaker IDE tooling will generate a template Web project complete with Oauth2
integration, samples pages and controllers to start building new glassware, as well
as allwoing you to create, edit and preview Timeline items called "Cards". IDE plug-ins,
and Card Editors will provide syntax highlighting, content assistance, error
tracing/debugging and similar capabilities, as appropriate. The glassware can be tested
and debugged directly from the IDE using a local application server or the Google App 
Engine. Finally, the glassware can be  published to the cloud right from the IDE.


The project has three near-term objectives: 

  - (1) Flatten the learning curve associated with using the Mirror API
  - (2) Support Java best practices for working with the Mirror API and
  - (3) Encourage sharing tools with the community

A longer-term objective will be to provide a home for a wider range of tools
to simplfify developing glassware using Java.
