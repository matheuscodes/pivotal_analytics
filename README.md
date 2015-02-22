# Pivotal Analytics

Pivotal Analytics - Tool for extracting statistical data from Pivotal Projects.

Copyright (C) 2014 Matheus Borges Teixeira

## About

Pivotal Analytics is a web tool for statistical observation and performance measurement of Pivotal Tracker Projects. It is build to measure team and projects in order to provide product or project managers better overview and control which Pivotal Tracker does not provide out of the box.

**License:** AGPL.

**API Version:** This project uses [Pivotal REST API v5](https://www.pivotaltracker.com/help/api/rest/v5)

_Pivotal Tracker_ is a project management tool from _[Pivotal Labs, Inc.](http://pivotallabs.com/)_

_Icon design by http://www.dryicons.com/_

#Tool Overview

##Overview
Displays statistics at project level, compiling a dashboard.

####Graphs
- **Team Velocity for Features:** plots the variation of the team's weekly velocity for features.
  For the calculation of a given week, the two previous weeks are taken into consideration, independently of iteration size.
  Note that this   only counts the features <strong>accepted</strong> during the course of the week and its storypoints.
- **Request Response Time in Days:** plots the time stories take to be completed, from the date of creation to date of acceptance.
  The graph looks into all stories **accepted** during the course of the week, plotting min, max and average of that week.
- **Backlog Daily Activity:** plots the number of closed and created stories on a daily basis.
- **Planning Daily Burn-Down:** plots the daily count of open stories for each iteration.
  _Note that special labels need to be added to planned tickets with [iteration_number] in order to make the graph work. This is necessary for Pivotal has no long-term follow up on iterations, and non completed tickets automatically move._

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/overview.png"/>

####Status
- **Pie Charts:** displays the distribution of the open stories in three categories.
  - Status (Backlog,  Started or Icebox);
  - Story Type (Bug, Feature, Chore or Release);
  - Among the labels  specified in the configuration.
- **List:** displays all open stories and their status, owner, labels and type.

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/overview_status.png"/>

##Starvation

Displays an overview of the waiting time in open stories.

- The number of days which a story has been waiting can be seen in the centre of the bar.
- The bar will be completely full on the course of one year wait.
- Any stories which have a label containing the words _on hold_ will be displayed in _grey_, otherwise in _blue_.
- The stories are divided in two groups: **Unscheduled** (_Icebox_) and **Scheduled** (_Backlog & Current_)
- There are a couple of filters which can be used, based on how much wait time is wanted.

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/starvation.png"/>

## Throughput

Displays a weekly overview of how many stories were requested and how many were accepted. There are four graphs, one which displays the overall count, and one for each story type (Bug, Feature and Chore).

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/throughput.png"/>

## Developers

Displays an overview based on the Owners of active stories.

#### Task Load

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/load.png" align="right"/>

If no _Owner_ is selected, an overview of the load for each unique _Owner_ of open stories in the project is displayed.

**Pivotal Analytics** looks into the entire available history and calculates the typical iteration delivery of the _Owner_ and compares with the current assigments.<br>

The load calculation takes into consideration both number of stories (all types) and its sizes (in case of features).

If the current assignment is over 100%, the bar will gradually change from _black_ to _red_.

When the colour becomes fully red means that the _Owner_ has **120% or more** of its typical iteration velocity assigned.

#### Developer Overview
Once a _Owner_ is selected, detailed information can be observed.

- **Story Distribution:** the current assignments are divided in three categories.
  - Story Type (Bugs, Features, Chores and Releases);
  - Story State (All possible states available in Pivotal);
  - Among the labels  specified in the configuration.
- **Delivery Count:** plots the weekly delivered story counts.
  An individual count for each Story Type is also available.
- **Velocity Overview:** plots the velocity only for features and its sizes.
- **Recent assigned Stories:** are the current and open stories assigned to the selected _Owner_.


<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/lucius.png"/>

## Planning Follow Up

Displays a detailed overview of each _planning_ or _iteration_.

- _Note that special labels need to be added to planned tickets with [iteration_number] in order to make this entire view to work. This is necessary for Pivotal has no long-term follow up on iterations, and non completed tickets automatically move._

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/followup_graph.png" align="right"/>

A list will be provided, with all iterations which have a proper label set, by clicking any of those, the completion statistics will be shown. They clarify how much of the iteration is complete and how much was actually delivered in time. Additionally, pie charts with distributions are displayed, giving absolute counts of the lists and the actual in-time deliveries.

Three further lists will be given:

- **Stories Planned for Iteration X:** lists all stories which contain the label [X] with its states, type, labels and owner.
- **Sidetracking Stories created and completed during Iteration X:** lists all stories which were created and accepted while the iteration X was ongoing.
- **Previously Accumulated Stories decluttered during Iteration X:** lists all other older stories, which were accepted while the iteration X was ongoing.

<img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/imgs/followup.png"/>

## All Stories

Displays a list with all downloaded stories in the project.

##Operation Instructions

### Purge <img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/icons/download.png">
This will remove all user data from Pivotal Analytics caches and delete all cookies.</p>

### Refresh <img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/icons/refresh.png">
**Pivotal Analytics** does not renew the Project snapshot on its own. If the user wishes to reload the project, this button must be triggered.

### Configurations <img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/icons/tools.png">

All parameters here are mandatory and need to be properly filled. In case of missing data, normal pages will not be loaded and user will be routed to this configuration screen.

- **User Token:** Can be obtained in Pivotal &gt; Profile. Used for authentication.
- **Project ID:** Pivotal ID of the project desired.
  Note that the above given _User Token_ must have access to this project.
  - Only one Project can be used per user at a time.
- **Special Labels:** A list of labels, separated by commas.
  Will be used for highlighting some porportions in the Project and Developer overviews.
- **Iteration to start Follow Up:** Number of the iteration where this tool will start reporting from.
  Note that it must be lower or equal than the Project current iteration, in case of wrong input the fallback is 1.
- **Date for Reference:** Specifies a date where the graphs will start plotting data.
  If the wrong format is specified, the fallback date is the oldest available in the project.

### Info and About <img src="https://raw.githubusercontent.com/matheuscodes/pivotal_analytics/master/src/main/webapp/icons/info.png">

Opens a page with the instructions and overview.
