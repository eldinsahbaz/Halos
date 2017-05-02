import PackageDescription

let package = Package(
    name: "Halos",
    dependencies: [
        .Package(url: "https://github.com/hkellaway/Gloss.git", majorVersion: 1, minor: 2)
    ]
)
