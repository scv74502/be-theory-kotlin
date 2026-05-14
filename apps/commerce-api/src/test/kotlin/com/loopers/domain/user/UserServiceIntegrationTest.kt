package com.loopers.domain.user

import com.loopers.domain.user.UserSteps.Companion.기본_로그인_ID
import com.loopers.domain.user.UserSteps.Companion.기본_비밀번호
import com.loopers.domain.user.UserSteps.Companion.기본_이메일
import com.loopers.domain.user.UserSteps.Companion.회원가입_커맨드_생성
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceIntegrationTest
    @Autowired
    constructor(
        private val userService: UserService,
        private val userJpaRepository: UserJpaRepository,
        private val databaseCleanUp: DatabaseCleanUp,
    ) {
        @AfterEach
        fun tearDown() {
            databaseCleanUp.truncateAllTables()
        }

        @Test
        fun `유효한_커맨드면_사용자가_저장된다`() {
            val result = userService.signUp(회원가입_커맨드_생성())

            val saved = userJpaRepository.findByLoginId(기본_로그인_ID)
            assertThat(result.loginId).isEqualTo(기본_로그인_ID)
            assertThat(saved).isNotNull
            assertThat(saved?.email).isEqualTo(기본_이메일)
        }

        @Test
        fun `중복_로그인ID로_가입하면_CONFLICT가_발생한다`() {
            userService.signUp(회원가입_커맨드_생성(loginId = "duplicate"))

            val ex = assertThrows<CoreException> {
                userService.signUp(회원가입_커맨드_생성(loginId = "duplicate", email = "other@example.com"))
            }
            assertThat(ex.errorType).isEqualTo(ErrorType.CONFLICT)
        }

        @Test
        fun `비밀번호는_원문이_아닌_암호화된_값으로_저장된다`() {
            userService.signUp(회원가입_커맨드_생성(rawPassword = 기본_비밀번호))

            val saved = userJpaRepository.findByLoginId(기본_로그인_ID)
            assertThat(saved?.password?.encoded).isNotEqualTo(기본_비밀번호)
        }
    }
