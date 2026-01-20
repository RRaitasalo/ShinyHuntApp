# ShinyHuntApp

An Android application for Pokémon shiny hunting. Track your shiny hunting progress, manage ongoing hunts, and keep a record of your captured shiny Pokémon.

## Features

### Hunt Management
- Start and track multiple shiny hunts simultaneously
- Support for various hunting methods (Random Encounter, Masuda Method, Radar, Soft Reset, etc.)
- Real-time encounter counting
- Hunt completion tracking with timestamps

### Pokémon Tracking
- Complete Pokédex integration with all Pokémon
- Track captured shiny Pokémon
- Display both regular and shiny sprites
- Filter by game availability and generation

### Game Integration
- Support for multiple Pokémon games
- Game-specific availability information
- Location and method tracking for each Pokémon

## Technology Stack

- **Platform**: Android (API 24+)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room (SQLite)
- **Networking**: Retrofit2 + Moshi
- **Async Processing**: Kotlin Coroutines
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **Web Scraping**: Jsoup

## App Architecture

```
├── UI Layer (Compose)
│   ├── Screens (Login, Main, Hunt, PokemonList, etc.)
│   └── Components (Cards, Navigation, etc.)
├── ViewModels
│   ├── PokemonViewModel
│   ├── HuntViewModel
│   └── LoginViewModel
├── Data Layer
│   ├── Repository
│   ├── Local (Room Database)
│   └── Network (PokeAPI)
└── Domain Models
    ├── Entities
    └── DTOs
```

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd ShinyHuntApp
```

2. Open in Android Studio
3. Sync Gradle files
4. Build and run on an Android device or emulator

## Usage

### Getting Started
1. Launch the app and create an account or use guest mode
2. Browse the Pokédex to find Pokémon you want to hunt
3. Start a new hunt by selecting a Pokémon and hunting method
4. Track encounters until you find a shiny!

### Hunt Methods Supported
- Random Encounter
- Masuda Method
- Radar (Chain fishing, etc.)
- Soft Reset
- DexNav
- SOS Calling
- Dynamax Adventure
- Outbreak
- Sandwich Method

## API Integration

The app integrates with the [PokeAPI](https://pokeapi.co/) to fetch:
- Pokémon data and sprites
- Type information
- Evolution chains
- Game-specific data

## Development Notes

### Data Sources
- Primary: PokeAPI v2
- Supplemental: Web scraping for game-specific data
- Local: Room database for offline functionality
