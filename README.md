fraggle
=======

A wrapper to Android Fragment Manager to encapsulate common operations concerning the fragment lifecycles.

## Philosophy

The idea of Fraggle is to reduce the boilerplate of having the managing code of the Fragment handling
inside your Activity and reduce and encapsulate it away; providing it as a kind of service inside it.

This allows to abstract your application's flow behavior from the actual management of it.

Download
--------

### Bundle

Fraggle comes bundled in `aar` format. Grab the latest bundle from [here](http://search.maven.org/remotecontent?filepath=com/sefford/fraggle/1.2.1/fraggle-1.2.1.aar)

### Maven

```XML
<dependency>
    <groupId>com.sefford</groupId>
    <artifactId>fraggle</artifactId>
    <version>3.0.0</version>
    <type>aar</type>
</dependency>
```

### Gradle 

```groovy
implementation 'com.sefford:fraggle:3.0.0'
```

Usage
-----

### Hooking Fraggle to your Activities

Fraggle consists of a number of extensions functions that allows you to use it with both native
and AndroidX FragmentManagers.

You only need to request a FragmentManager or inject it using your DI framework choice through
the normal means to allow it.

### Hooking Fraggle to your Fragments

Fraggle has an interface `FraggleFragment` to extend your Fragments with certain functionalities.

The first one is to enforce the Fragment to self-identify through the `getFragmentTag` method. This
method is intended to provide the runtime identification for both logging purposes and as way for
Fraggle to look up Fragments in the backstack.

Another functionality available to FraggleFragments is to decide if there are customized behaviors 
for clicking the Back Button using `hasCustomizedBackAction()` and `onBackPressed()` methods. The first
is used to identify if the FraggleFragment itself has a customized action for clicking the back button,
and the second to actually execute it. 

The conditions will be only evaluated on the moment the user clicks on the back button, so the behavior
can be dynamically updated depending on certain conditions of the view. 

Additionally the FraggleFragment can signal the FraggleManager to make a jump of several Fragments in
the backstack providing such Fragments exist, by looking up the backstack for a certain Fragment tag.
In order to activate this functionality the FraggleFragment must override `onBackPressedTarget()`
method with a known tag. This can be used to perform hierarchical navigation or returning from long
processes or error states to past Fragments. As with the previous point, this behavior can be changed
in runtime depending of the flow of the application.

There is another addition in order to prevent endless cycles of navigation through your flow. This is
`isSingleInstance()` method. It will signal the FraggleManager that before adding a new instance
it should try to find and pop an existing instance of such Fragment. 

In Fraggle there is a concept  known in Fraggle as `Entry Fragment`. An `Entry Fragment` is a Fragment such
as popping back from the back stack should signal your activity to `finish()`. This is achieved by
letting Fragments override `isEntryFragment()` method.

FraggleFragment interface extends the Fragment lifecycle with `onFragmentVisible()` and `onFragmentNotVisible()`
methods for certain situations.
 
### Instantiating new Fragments

The FraggleManager API is simple. This is done through five concepts

- Target fragment to add.
- Tag of the Fragment (can be obtained from the instance using `fragmentTag`, but this way we give a chance to change it.
- Fragment Animations bundle (optional) to add some eye candy to the transitions.
- Configuration flags for the addition of the Fragment.
- Target container ID.

The FraggleManager will configure properly the Fragment and perform the transaction.

Although you can use `addFragment` API manually, you can bundle it through the concept of `Navigators`. 

These navigators will bundle and abstract the instantiation of a Fragment. 
 
The available flags for configuring a Fragment are:

- `ADD_TO_BACKSTACK`: Default behavior. The Fragment will be added to the backstack.
- `DO_NOT_ADD_TO_BACKSTACK`: The Fragment will not be added to the backstack.
- `CLEAR_BACKSTACK`: The backstack will be cleared before the Fragment is added.
- `DO_NOT_REPLACE_FRAGMENT`: The default behavior of FraggleManager is to replace the current Fragment for the new one. 
However, sometimes you will want to show the new over the current one, but when you pop back the old one, the Fragment
lifecycle will not kick on because of you never removed the Fragment in the first place, for this situation overriding
the `onFragmentVisible()` extension will come in handy.

### Popping back Fragments

Before popping back a Fragment, Fraggle will check out some conditions about the Fragment on top of the backstack.

1. Know if the Fragment has a customized code that the Fragment must execute, then execute it.
2. If not, check if it has to perform a jump by reading the `onBackPressedTarget()` tag. 
3. Execute the correct jump whether is popping a single element from the back stack, or a particular element.

Bear in mind that performing a `finish()` in the activity is not the responsibility of the FraggleManager,
and the developer should take care of the correct conditions for his Activity to correctly finish. However,
the Activity can query the FraggleManager with `peek()` for the top Fragment and see if the `isEntryFragment()` method returns
true.

Sample
------

You can find a sample project showcasing Fraggle capabilities at [this url](https://github.com/Sefford/fraggle-sample)

License
-------
    Copyright 2014 Sefford.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.






