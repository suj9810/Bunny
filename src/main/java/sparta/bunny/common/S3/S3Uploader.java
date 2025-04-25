package sparta.bunny.common.S3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {

	// AWS S3 클라이언트 의존성 주입
	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucket;

	// MultipartFile을 받아서 S3에 업로드하고 URL을 반환
	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		// MultipartFile -> File 변환
		File uploadFile = convert(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		// S3 업로드 수행
		return upload(uploadFile, dirName);
	}

	// 실제 업로드 로직 (디렉토리명 포함된 파일명 생성 → S3 업로드 → 로컬 파일 삭제 → URL 반환)
	private String upload(File uploadFile, String dirName) {
		// 예: images/uuid_filename.png
		String fileName = dirName + "/" + changedImageName(uploadFile.getName());
		// S3 업로드 및 URL 반환
		String uploadImageUrl = putS3(uploadFile, fileName);
		// 로컬에 임시로 생성된 파일 삭제
		removeNewFile(uploadFile);
		return uploadImageUrl;
	}

	// S3에 파일 업로드
	private String putS3(File uploadFile, String fileName) {
		// PublicRead 없이 기본 설정으로 업로드 (정책에 따라 변경 가능)
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile)
		);
		// 업로드한 파일의 URL 반환
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	// 로컬에 임시로 생성된 파일 삭제
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("로컬 파일 삭제 완료: {}", targetFile.getAbsolutePath());
		} else {
			log.warn("로컬 파일 삭제 실패: {}", targetFile.getAbsolutePath());
		}
	}

	// MultipartFile → File로 변환 (임시 디렉토리에 UUID로 파일 생성)
	private Optional<File> convert(MultipartFile file) throws IOException {
		// 원래 파일 이름이 없을 경우 "tempFile" 사용
		String originalName = file.getOriginalFilename();
		String fileName = UUID.randomUUID() + "_" + (originalName != null ? originalName : "tempFile");
		File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);

		if (tempFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				fos.write(file.getBytes()); // 파일 데이터 작성
			}
			return Optional.of(tempFile);
		} else {
			log.error("파일 생성 실패: {}", tempFile.getAbsolutePath());
			return Optional.empty();
		}
	}

	// 파일 이름을 UUID 기반으로 변경하여 중복 방지
	private String changedImageName(String originName) {
		return UUID.randomUUID().toString() + "_" + originName;
	}

	// 삭제
	public void delete(String fileUrl) {
		String encodedKey = extractKeyFromUrl(fileUrl);
		String decodedKey = URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
		try {
			amazonS3Client.deleteObject(bucket, decodedKey);
		} catch (Exception e) {
			throw new RuntimeException("이미지 삭제에 실패하였습니다.");
		}
	}

	private String extractKeyFromUrl(String fileUrl) {
		// "https://bucket-name.s3.region.amazonaws.com/" 다음부터가 key
		int startIndex = fileUrl.indexOf(".com/") + 5;
		return fileUrl.substring(startIndex); // images/xxx.png
	}

}
