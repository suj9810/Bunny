package sparta.bunny.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.S3.S3Uploader;

@Service
@RequiredArgsConstructor
public class FileService {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

	private final S3Uploader s3Uploader;

	/**
	 * 다수의 이미지를 S3에 업로드하고,
	 * 업로드된 이미지 URL을 기반으로 엔티티를 생성하여 리스트로 반환하는 메서드
	 *
	 * @param files 업로드할 이미지 파일 리스트
	 * @param directory S3에 저장할 디렉토리 경로 (예: "review-images", "menu-images")
	 * @param entityCreator 업로드된 URL을 받아서 엔티티 객체를 생성하는 함수 (람다식)
	 *                      - 입력: 업로드된 이미지 URL (String)
	 *                      - 출력: 엔티티 객체 (예: ReviewImage, MenuImage)
	 * @return 업로드된 이미지에 대한 엔티티 리스트
	 * @throws IOException 파일 업로드 중 발생할 수 있는 예외
	 */
	public <T> List<T> uploadAndCreateEntities(
		List<MultipartFile> files,
		String directory,
		Function<String, T> entityCreator
	) throws IOException {

		// 결과를 담을 엔티티 리스트
		List<T> entityList = new ArrayList<>();

		for (MultipartFile file : files) {
			// 1. 확장자 및 크기 유효성 검사
			validateImageExtension(file);

			// 2. S3에 파일 업로드 후 이미지 URL 획득
			String uploadedImageUrl = s3Uploader.upload(file, directory);

			// 3. 람다 함수 실행 → 이미지 URL을 기반으로 엔티티 객체 생성
			T imageEntity = entityCreator.apply(uploadedImageUrl);

			// 4. 생성된 엔티티를 결과 리스트에 추가
			entityList.add(imageEntity);
		}

		// 5. 모든 이미지 엔티티 리스트 반환
		return entityList;
	}

	public <T> void deleteS3Images(
		List<T> imagesEntity,
		Function<T, String> urlExtractor
	) {
		for (T t : imagesEntity) {
			String url = urlExtractor.apply(t);
			s3Uploader.delete(url);
		}
	}

	private void validateImageExtension(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();

		// 파일 크기 검사
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB까지 업로드할 수 있습니다.");
		}

		// 파일 확장자 검사
		if (originalFilename == null || !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")
			|| originalFilename.endsWith(".png"))) {
			throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다.");
		}
	}
}
