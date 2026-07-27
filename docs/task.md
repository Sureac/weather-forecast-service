### Aim of this coding task

The aim of this coding task is to assess the way you approach problems and design solutions, as well as providing
insight into your coding style, expertise and willingness to experiment. It will also provide us with a common ground
for a technical interview.

We'd love to see what kind of solution you come up with to the task below and how you approach problem solving.

There is no hard time limit set for this task, but generally one evening should be the goal. Due to time constraints, we
don't expect a perfect solution with all the edge cases covered. You’re encouraged to focus on your core strengths and
things that you think are important for production service — just leave notes and TODOs if there are parts of
implementation you didn’t manage to complete.

Your feedback is important to us, so let us know what you think about the task — before you’ve started or after you’re
done ☺️

### Task Description

We recently launched a feature in the Spond app where weather forecasts are shown for your upcoming events, as seen in
the screenshot on the right.

Imagine you are a developer at Spond who has been tasked with implementing a Java based RESTful API service to power
this feature in the app.

Your version of this feature should be able to show:

- air temperature, in Celsius
- wind speed, in m/s
  for any event that starts in the next 7 days and has a location set.

The [met.no Located Forecast API](https://api.met.no/weatherapi/locationforecast/2.0/documentation) should be used to provide weather forecast information.

Remember, weather forecasts change over time so the forecast information shown to users should be no more than 2 hours
old.

### Background information and considerations:

- Spond events store their location as a latitude and longitude value, which is available to the app.
- Every Spond event has a start timestamp and an end timestamp, both in UTC, which are available to the app. Events
typically last for up to 2 hours.
- The event screen is opened, on relevant events, up to 80 times per second at peak times.
- Consider the [locationforecast API terms of service](https://api.met.no/doc/TermsOfService) and how that might impact the design of your solution.
- We prefer that you do not use an AI assistant for this code assignment. If you choose to use one anyway, please explain
why and how you used it.

### Final result should consist of:

- Source code with instructions on how to run it in a Git repository we can access (Github, Bitbucket etc.)
- Extra points for test coverage and a simple way to run the service locally.
- Feel free to add the README section with improvements to the code you’d think would be the next natural steps.
- Consider how this service could be deployed and managed in a production environment, so that we can discuss it in the
interview. 
