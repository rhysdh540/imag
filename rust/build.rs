use std::env;
use std::path::PathBuf;
use walkdir::WalkDir;

fn main() {
    // compile_ect();
    // bind_ect();
}

fn compile_ect() {
    let mut files = Vec::new();
    // search through ./Efficient-Compression-Tool/src for c, cpp, and h files
    WalkDir::new("./Efficient-Compression-Tool/src").into_iter().map(|entry| {
        entry.unwrap().path().to_path_buf()
    }).filter(|path| {
        path.extension().map(|ext| ext == "c" || ext == "cpp" || ext == "h").unwrap_or(false)
    }).for_each(|path| {
        files.push(path);
    });

    let mut builder = cc::Build::new();
    for file in files {
        builder.file(file);
    }

    builder.opt_level(3)
        .cpp(true)
        .flag_if_supported("-ansi")
        .flag_if_supported("-pedantic")
        .flag_if_supported("-Wno-unused-function")
        .flag_if_supported("-c")
        .debug(true)
        .compile("ect");
}

fn bind_ect() {
    let ect = bindgen::Builder::default()
        .clang_args(&[
            "-x",
            "c++",
            "-std=c++14",
        ])
        .header("./Efficient-Compression-Tool/src/main.h")
        .parse_callbacks(Box::new(bindgen::CargoCallbacks::new()))
        .generate()
        .expect("Unable to generate bindings for ect");

    let out_path = PathBuf::from(env::var("OUT_DIR").unwrap());
    ect.write_to_file(out_path.join("ect_bindings.rs"))
        .expect("Couldn't write bindings!");
}