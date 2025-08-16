package kh.devspaceapi.controller;

import kh.devspaceapi.comm.response.ApiResponse;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {
    private List<Integer> samples = new ArrayList<>();
    TestController() {
        for (int i = 0; i < 5; i++) {
            samples.add(i);
        }
    }

    // NO AUTH
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Integer>>> getSamples() {
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    // NO AUTH
    @PostMapping("")
    public ResponseEntity<ApiResponse<List<Integer>>> addSample(@RequestBody TestRequestDto request) {
        samples.add(request.getNumber());
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    // NO AUTH
    @PutMapping("{index}")
    public ResponseEntity<ApiResponse<List<Integer>>> modifySample(@PathVariable int index, @RequestBody TestRequestDto request) {
        if (!samples.isEmpty()) {
            samples.set(index, request.getNumber());
        }
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    // NO AUTH
    @DeleteMapping("{index}")
    public ResponseEntity<ApiResponse<List<Integer>>> removeSample(@PathVariable int index) {
        if (!samples.isEmpty()) {
            samples.remove(index);
        }
        return ResponseEntity.ok(ApiResponse.success(samples));
    }
    
    // Token 필요
    @GetMapping("/token")
    public ResponseEntity<ApiResponse<List<Integer>>> getSamplesWithToken() {
        return ResponseEntity.ok(ApiResponse.success(samples));
    }

    @Getter
    @Setter
    static
    class TestRequestDto {
        private int number;
    }
}
